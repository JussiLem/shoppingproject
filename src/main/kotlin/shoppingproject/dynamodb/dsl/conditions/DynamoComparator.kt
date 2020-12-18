package shoppingproject.dynamodb.dsl.conditions

import com.amazonaws.services.dynamodbv2.model.Condition

interface DynamoComparator {
    fun toCondition(): Condition
}