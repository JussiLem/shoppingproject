package shoppingproject.dynamodb.dsl.filters

import shoppingproject.dynamodb.dsl.conditions.DynamoComparator

class ConcreteFilterBuilder: FilterQueryBuilder {

    lateinit var dynamoFunction: DynamoFunction
    lateinit var comparator: DynamoComparator

    override fun build(): FilterQuery {
        return ConcreteFilter(dynamoFunction!!, comparator)
    }
}