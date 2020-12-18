package shoppingproject.dynamodb.dsl.conditions

import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import shoppingproject.dynamodb.dsl.toAttributeValue

class LessThanOrEquals(override val right: Any): SingleValueDynamoCompator{

    override fun toCondition(): Condition {
        return Condition().withComparisonOperator(ComparisonOperator.LE).withAttributeValueList(toAttributeValue(right))
    }
}