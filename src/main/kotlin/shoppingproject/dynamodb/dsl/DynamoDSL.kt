package shoppingproject.dynamodb.dsl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder


class DynamoDSL(val dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient()){
    fun query(tableName: String, block: QueryIteratorBuilder.() -> Unit): QueryIterator {
        val queryBuilder = QueryIteratorBuilder(dynamoDB)
        queryBuilder.tableName = tableName
        block(queryBuilder)
        val queryIterator = queryBuilder.build()
        //Initial query
        queryIterator.query()
        return queryIterator
    }
}