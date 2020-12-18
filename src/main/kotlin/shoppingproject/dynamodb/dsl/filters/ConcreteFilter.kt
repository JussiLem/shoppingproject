package shoppingproject.dynamodb.dsl.filters

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import shoppingproject.dynamodb.dsl.conditions.*
import shoppingproject.dynamodb.dsl.toAttributeValue
import java.util.*
import kotlin.streams.asSequence

class ConcreteFilter(
    private val dynamoFunction: DynamoFunction,
    private val comparator: DynamoComparator? = null
): FilterQuery {

    fun getFilterRequestProperties(): FilterRequestProperties {

        val expressionAttributeValues: MutableMap<String, AttributeValue> = mutableMapOf()
        val expressionAttributeNames: MutableMap<String, String> = mutableMapOf()
        var filterExpression = ""

        when (dynamoFunction) {
            is Attribute -> {
                val expressionAttributeName = toExpressionAttributeName(dynamoFunction.attributeName)
                filterExpression += expressionAttributeName
                expressionAttributeNames[expressionAttributeName] = dynamoFunction.attributeName

                fun singleValueComparator(operator: String, comparator: SingleValueDynamoCompator) {
                    val expressionAttributeValue = toExpressionAttributeValue(dynamoFunction.attributeName)
                    filterExpression += " $operator $expressionAttributeValue"
                    expressionAttributeValues[expressionAttributeValue] = toAttributeValue(comparator.right)
                }

                when(comparator) {
                    is Equals -> singleValueComparator("=", comparator)
                    is NotEquals -> singleValueComparator("<>", comparator)
                    is GreaterThan -> singleValueComparator(">", comparator)
                    is GreaterThanOrEquals -> singleValueComparator(">=", comparator)
                    is LessThan -> singleValueComparator("<", comparator)
                    is LessThanOrEquals -> singleValueComparator("<=", comparator)
                    is Between -> {
                        val leftExpressionAttributeValue = toExpressionAttributeValue(dynamoFunction.attributeName + "left")
                        val rightExpressionAttributeValue = toExpressionAttributeValue(dynamoFunction.attributeName + "right")
                        filterExpression += " BETWEEN $leftExpressionAttributeValue AND $rightExpressionAttributeValue "
                        expressionAttributeValues[leftExpressionAttributeValue] = toAttributeValue(comparator.left)
                        expressionAttributeValues[rightExpressionAttributeValue] = toAttributeValue(comparator.right)
                    }
                    is InList -> {
                        val listOfAttributeValues = comparator.right.joinToString {
                            val expressionAttributeValue = toExpressionAttributeValue(dynamoFunction.attributeName)
                            expressionAttributeValues[expressionAttributeValue] = toAttributeValue(it)
                            expressionAttributeValue
                        }

                        filterExpression += " IN (${listOfAttributeValues})"

                    }
                }
            }

            is AttributeExists -> {
                val expressionAttributeName = toExpressionAttributeName(dynamoFunction.attributeName)
                filterExpression += "attribute_exists(${expressionAttributeName})"
                expressionAttributeNames[expressionAttributeName] = dynamoFunction.attributeName
            }
        }

        return FilterRequestProperties(expressionAttributeValues, filterExpression, expressionAttributeNames)
    }

    private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val random = Random()

    private fun toExpressionAttributeName(attributeName: String): String =
        "#" + attributeName.filter { it in ('a' until 'z') + ('A' until 'Z') } + nonce()

    private fun toExpressionAttributeValue(attributeName: String): String =
        ":" + attributeName.filter { it in ('a' until 'z') + ('A' until 'Z') } + nonce()

    private fun nonce(length: Long = 5): String =
        random.ints(length, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
}