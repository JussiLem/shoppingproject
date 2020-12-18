package shoppingproject.dynamodb.dsl.conditions

import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import shoppingproject.dynamodb.dsl.toAttributeValue

class LessThan(override val right: Any): SingleValueDynamoCompator {

    override fun toCondition(): Condition {
        return Condition().withComparisonOperator(ComparisonOperator.LT).withAttributeValueList(toAttributeValue(right))
    }
}