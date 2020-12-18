package shoppingproject.dynamodb.dsl.conditions

interface SingleValueDynamoCompator: DynamoComparator {
    val right: Any
}