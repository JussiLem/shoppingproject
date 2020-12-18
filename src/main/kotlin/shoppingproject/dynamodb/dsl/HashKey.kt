package shoppingproject.dynamodb.dsl

import shoppingproject.dynamodb.dsl.conditions.Equals

@DynamoDSLMarker
class HashKey(val keyName: String, val equals: Equals)

@DynamoDSLMarker
class HashKeyBuilder(val keyName: String){
    var comparator: Equals? = null
    fun build(): HashKey = HashKey(keyName, comparator!!)
}

fun HashKeyBuilder.eq(value: Any){
    comparator = Equals(value)
}