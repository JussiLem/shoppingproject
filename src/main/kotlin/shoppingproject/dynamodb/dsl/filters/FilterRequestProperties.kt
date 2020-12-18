package shoppingproject.dynamodb.dsl.filters

import com.amazonaws.services.dynamodbv2.model.AttributeValue

class FilterRequestProperties(val expressionAttributeValues: Map<String, AttributeValue>,
                              val filterExpression: String,
                              val expressionAttributeNames: Map<String, String>)