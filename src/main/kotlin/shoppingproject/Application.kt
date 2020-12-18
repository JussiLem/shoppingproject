package shoppingproject

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.*
import shoppingproject.dynamodb.batch.DynamoBatchExecutor
import shoppingproject.mappers.AccountMapper
import shoppingproject.mappers.PriceMapper
import shoppingproject.model.Account
import shoppingproject.model.Product
import java.util.concurrent.atomic.AtomicLong
import io.ktor.serialization.*
import shoppingproject.health.Health

@ExperimentalCoroutinesApi
val myContext = newSingleThreadContext("MyOwnThread")

@ExperimentalCoroutinesApi
fun Application.module() {
    // This adds automatically Date and Server headers to each response, and would allow you to configure
    // additional headers served to each response.
    install(DefaultHeaders)

    // This uses use the logger to log every call (request/response)
    install(CallLogging) {
        callIdMdc("request-id")
    }

    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(Health)

    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        generate { newRequestId() }
        verify { it.isNotEmpty() }
    }

    routing {
        get("/account") {
            val deferred = async(myContext) {
                call.respondAccountQuery()
            }
            deferred.await()
        }

        post("/account") {
            try {
                val post = call.receive<Account>()
                application.log.debug("Account received", post.toString())
                val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
                val dynamoBatch: DynamoBatchExecutor<Account> = DynamoBatchExecutor(amazonDynamoDB)
                dynamoBatch.persist(listOf(Account(post.name, post.email)), AccountMapper(), "jussi-account")
                call.respond(mapOf("OK" to true))
            } catch (e: Exception) {
                application.log.error("Failed to register account", e)
                call.respond(HttpStatusCode.BadRequest, "Problem creating Account")
            }
        }
        post("/product") {
            try {
                val product = call.receive<Product>()
                application.log.debug("Product", product.toString())
                val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
                val dynamoBatch: DynamoBatchExecutor<Product> = DynamoBatchExecutor(amazonDynamoDB)

                dynamoBatch.persist(
                    listOf(Product(product.name, product.type, product._price)),
                    PriceMapper(),
                    "jussi-product"
                )
                call.respond(mapOf("OK" to true))
            } catch (e: Exception) {
                application.log.error("Failed to register account", e)
                call.respond(HttpStatusCode.BadRequest, "Problem creating Account")
            }
        }
    }

}

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun dynamoDb(): AmazonDynamoDB {
    return AmazonDynamoDBAsyncClientBuilder.standard()
        .withCredentials(ProfileCredentialsProvider("bdd"))
        .withRegion(Regions.EU_WEST_1).build()
}

private val lastIncrement = AtomicLong()
private fun newRequestId(): String = "shoppingproject-id-${lastIncrement.incrementAndGet()}"

