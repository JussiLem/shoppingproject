package shoppingproject.dynamodb.dsl.conditions

import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import shoppingproject.dynamodb.dsl.toAttributeValue

class InList(val right: List<Any>): DynamoComparator{

    override fun toCondition(): Condition {
        //Is this right?
        return Condition().withComparisonOperator(ComparisonOperator.IN).withAttributeValueList(toAttributeValue(right))
    }
}