package shoppingproject.dynamodb.dsl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.QueryRequest
import com.amazonaws.services.dynamodbv2.model.QueryResult
import shoppingproject.dynamodb.dsl.filters.RootFilter

class QueryIterator(
    private val dynamoDB: AmazonDynamoDB,
    private val tableName: String,
    private val hash: HashKey,
    private val sort: SortKey?,
    private val filtering: RootFilter?): Iterator<Map<String, AttributeValue>>{ //Make it generic

    var lastEvaluatedKey: Map<String, AttributeValue> = emptyMap()
    private val results: MutableList<Map<String, AttributeValue>> = mutableListOf()

    private var index: Int = 0

    override fun hasNext(): Boolean {
        return index < results.size || lastEvaluatedKey.isNotEmpty()
    }

    override fun next(): Map<String, AttributeValue> {
        if(index >= results.size && lastEvaluatedKey.isNotEmpty()){
            query()
            return next()
        }
        if(index >= results.size){
            throw RuntimeException("No more elements")
        }
        else{
            val toReturn = results[index]
            index++
            return toReturn
        }
    }

    fun query(){

        val request = QueryRequest()
        request.withTableName(tableName)

        if(sort == null){
            request.withKeyConditions(mapOf(Pair(hash.keyName, hash.equals.toCondition())))
        }
        else{
            request.withKeyConditions(mapOf(Pair(hash.keyName, hash.equals.toCondition()), Pair(sort.sortKeyName, sort.comparisonOperator.toCondition())))
        }

        if(filtering != null) {
            val props = filtering.getFilterRequestProperties()

            request.withFilterExpression(props.filterExpression)
            if(props.expressionAttributeNames.isNotEmpty()){
                request.withExpressionAttributeNames(props.expressionAttributeNames)
            }
            if(props.expressionAttributeValues.isNotEmpty()){
                request.withExpressionAttributeValues(props.expressionAttributeValues)
            }
        }

        //Returns last evaulated key
        val result: QueryResult = dynamoDB.query(request)

        results.addAll(result.items)
        lastEvaluatedKey = if(result.lastEvaluatedKey == null){
            emptyMap()
        } else{
            result.lastEvaluatedKey
        }

    }

}