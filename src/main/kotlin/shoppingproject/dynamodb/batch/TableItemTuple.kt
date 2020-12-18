package shoppingproject.dynamodb.batch

import com.amazonaws.services.dynamodbv2.model.WriteRequest

class TableItemTuple(val tableName: String, val writeRequest: WriteRequest)
