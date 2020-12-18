package shoppingproject.dynamodb.dsl.conditions

import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import shoppingproject.dynamodb.dsl.toAttributeValue

class Between(val left: Any, val right: Any): DynamoComparator{
    val value = ComparisonOperator.BETWEEN

    override fun toCondition(): Condition {
        return Condition().withComparisonOperator(ComparisonOperator.BETWEEN).withAttributeValueList(toAttributeValue(left), toAttributeValue(right))
    }
}