package shoppingproject.dynamodb.dsl

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import shoppingproject.dynamodb.dsl.filters.RootFilter

@DynamoDSLMarker
class QueryIteratorBuilder(val dynamoDB: AmazonDynamoDB) {
    lateinit var tableName: String
    lateinit var hashkey: HashKey
    lateinit var sortKey: SortKey
    lateinit var filtering: RootFilter

    fun build(): QueryIterator  {
        return QueryIterator(dynamoDB, tableName!!, hashkey!!, sortKey, filtering)
    }
}

fun QueryIteratorBuilder.hashKey(keyName: String, block: HashKeyBuilder.() -> Unit) {
    hashkey = HashKeyBuilder(keyName).apply(block).build()
}

fun QueryIteratorBuilder.sortKey(keyName: String, block: SortKeyBuilder.() -> Unit) {
    sortKey = SortKeyBuilder(keyName).apply(block).build()
}