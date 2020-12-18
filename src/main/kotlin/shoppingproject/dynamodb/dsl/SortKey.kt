package shoppingproject.dynamodb.dsl

import shoppingproject.dynamodb.dsl.conditions.Between
import shoppingproject.dynamodb.dsl.conditions.ComparableBuilder
import shoppingproject.dynamodb.dsl.conditions.DynamoComparator
import shoppingproject.dynamodb.dsl.conditions.Equals

@DynamoDSLMarker
class SortKey(val sortKeyName: String,
              val comparisonOperator: DynamoComparator
): ComparableBuilder


@DynamoDSLMarker
class SortKeyBuilder(val keyName: String){
    var comparator: DynamoComparator? = null
    fun build(): SortKey = SortKey(keyName, comparator!!)
}

fun SortKeyBuilder.between(values: Pair<Any, Any>){
    comparator = Between(values.first, values.second)
}

fun SortKeyBuilder.eq(value: Any){
    comparator = Equals(value)
}