package shoppingproject.dynamodb.batch

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import io.reactivex.Observable.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

private const val BATCH_SIZE = 25 // DynamoDB's BatchWriteItem allows a max of 25 items per batch
sealed class Result<T, E>(
    open val error: E? = null,
    open val data: T? = null
) {
    data class Success<T>(
        override val data: T
    ): Result<T, Nothing?>()

    data class Error<E>(
        override val error: E
    ): Result<Nothing?, E>()
}

fun <E> E.wrapWithError(): Result.Error<E> = Result.Error(this) // similar to `Either.asLeft()`
fun <T> T.wrapWithSuccess(): Result.Success<T> = Result.Success(this)  // similar to `Either.asRight()`


/**
 *
 */
open class DynamoBatchExecutor<T>(private val amazonDynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient(),
                                  private val scheduler: Scheduler =  Schedulers.io(),
                                  private val random: Random = Random(),
                                  private val initialBackoffMs: Long = 1000,
                                  private val attemptLimit: Long? = null
) {
    private val logger = LoggerFactory.getLogger(DynamoBatchExecutor::class.java)
    /**
     * Deletes the [items] of type[T] that in the table [tableName]
     * The function [primaryKeyFunction] should returns Map<String, AttributeValue>
     * It is up to the consumer the ensure that there are no duplicates
     */
    fun delete(items: List<T>, primaryKeyFunction: (T)-> Map<String, AttributeValue>, tableName: String) {

        val writeRequests =
            items.map { record ->
                WriteRequest()
                    .withDeleteRequest(DeleteRequest().withKey(primaryKeyFunction(record)))
            }.map { writeRequest -> TableItemTuple(tableName, writeRequest) }

        batchPersist(writeRequests)
    }


    /**
     * Batch persist the [items] to table [tableName] where [mapper] converts objects of type [T] to Map<String, AttributeValue>
     */
    fun persist(items: List<T>,
                mapper: DynamoMapper<T>,
                tableName: String) {

        val writeList = getWriteRequests(items, mapper)
        persist(writeList.map { TableItemTuple(tableName, it) } )
    }

    /**
     * Batch persist the [items] to table [tableName] where [items] are in the form Map<String, AttributeValue>
     */
    fun persist(items: List<Map<String, AttributeValue>>,
                tableName: String) {

        val writeList = items
            .map { WriteRequest().withPutRequest(PutRequest().withItem(it)) }

        persist(writeList
            .map { TableItemTuple(tableName, it) })
    }

    /**
     * Batch persist a list of TableItemTuple.
     * This is a bit more low level, but it means you can do batch writes to multiple different tables
     *
     */
    fun persist(writeList: List<TableItemTuple>): List<PublishSubject<RetryablePut>> {

        //TODO How to accumultate finite observable into list
        val listOfSubjects = ArrayList<PublishSubject<RetryablePut>>()

        // Split into batches and then persist to Dynamo
        fromIterable(writeList)
            .buffer(BATCH_SIZE)
            .forEach { v -> listOfSubjects.add(this.batchPersist(v)) }
        return listOfSubjects
    }

    /**
     * Batch persist a list of items with a callback function when they are all complete
     */
    fun persistWithCallbackOnComplete(items: List<T>,
                                      mapper: DynamoMapper<T>,
                                      tableName: String,
                                      complete: (Instant) ->Void) {
        val writeList = getWriteRequests(items, mapper)

        val result = persist(writeList.map { w -> TableItemTuple(tableName, w) })
            .map { it to false }
            .toMap()
            .toMutableMap()

        result.forEach { (subject, _) ->

            val consumer: (Any?) -> (Unit) = @Synchronized {
                result[subject] = true
                val allComplete = result.entries.all { it.value }
                if (allComplete) {
                    //No parameter, no return value, pure side-effect
                    complete(Instant.now())
                }
            }

            subject.doOnComplete { consumer(null) }

            //Case where it was finished before method returned
            if (subject.hasComplete()) {
                consumer(null)
            }
        }
    }

    private fun getWriteRequests(items: List<T>,
                                 mapper: DynamoMapper<T>): List<WriteRequest> {
            return items
                .map { mapper.mapToDynamoItem(it) }
                .map { WriteRequest().withPutRequest(PutRequest().withItem(it)) }
    }

    private fun batchPersist(writeList: List<TableItemTuple>): PublishSubject<RetryablePut> {
        val subject = PublishSubject.create<RetryablePut>()

        subject.subscribe { retry ->
            val batchWriteItemRequest = BatchWriteItemRequest()
            val requestItems = retry.items.groupBy({ it.tableName }, { it.writeRequest })

            batchWriteItemRequest.requestItems = requestItems
            try {
                val result = amazonDynamoDB.batchWriteItem(batchWriteItemRequest)
                // Partial failure
                if (result.unprocessedItems.isNotEmpty()) {

                    val retryRequests =
                        result.unprocessedItems
                            .entries
                            .flatMap { entry ->
                                entry.value.map { value -> TableItemTuple(entry.key, value) }
                            }

                    createRetry(retry.attempt, retryRequests, subject)
                } else {
                    // Full Success
                    subject.onComplete()
                }
            } catch (e: ProvisionedThroughputExceededException) {
                createRetry(retry.attempt, writeList, subject)
            } catch (e: Exception) {
                print(e)
            }
            // Total failure: Retry the whole lot
        }
        subject.onNext(RetryablePut(0, writeList))
        return subject
    }

    private fun createRetry(attempt: Int,
                            writeList: List<TableItemTuple>,
                            subject: PublishSubject<RetryablePut>
    ) {
        if(attemptLimit != null && attemptLimit < attempt){
            subject.onError(RuntimeException("Max attempt limit exceeded"))
        }
        else{
            timer(getExponentialBackoffWithJitter(attempt), TimeUnit.MILLISECONDS, scheduler)
                .subscribe { subject.onNext(RetryablePut(attempt + 1, writeList)) }
        }
    }

    // See: https://www.awsarchitectureblog.com/2015/03/backoff.html
    private fun getExponentialBackoffWithJitter(attempt: Number): Long =
        random.longs(0, (initialBackoffMs * 2.0.pow(attempt.toDouble())).toLong())
            .findFirst()
            // Should never happen, but fatal if it does
            .orElseThrow { RuntimeException("No random int found") }
}