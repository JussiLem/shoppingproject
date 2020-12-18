package shoppingproject.dynamodb.batch

import com.amazonaws.services.dynamodbv2.model.AttributeValue

interface DynamoMapper<T> {
    fun mapToDynamoItem(t: T): Map<String, AttributeValue>
}