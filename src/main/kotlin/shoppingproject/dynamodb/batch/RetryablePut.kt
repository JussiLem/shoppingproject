package shoppingproject.dynamodb.batch

class RetryablePut(val attempt: Int, val items: List<TableItemTuple>)