package shoppingproject.dynamodb.dsl.conditions

infix fun Number.AND(other:  Number): Pair<Number, Number> = Pair(this, other)
infix fun String.AND(other:  String): Pair<String, String> = Pair(this, other)