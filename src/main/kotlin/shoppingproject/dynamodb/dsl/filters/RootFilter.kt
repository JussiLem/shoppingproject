package shoppingproject.dynamodb.dsl.filters

import com.amazonaws.services.dynamodbv2.model.AttributeValue

class RootFilter(val filterQuery: List<FilterConnection>) : FilterQuery {
    fun getFilterRequestProperties(): FilterRequestProperties{

        val expressionAttributeValues: MutableMap<String, AttributeValue> = mutableMapOf()
        val expressionAttributeNames: MutableMap<String, String> = mutableMapOf()
        var filterExpression = ""

        val filterClosure : (FilterQuery) -> Unit = {
            when(it){
                is RootFilter -> {
                    val nestedProperties = it.getFilterRequestProperties()
                    filterExpression += "(${nestedProperties.filterExpression})"
                    expressionAttributeValues.putAll(nestedProperties.expressionAttributeValues)
                    expressionAttributeNames.putAll(nestedProperties.expressionAttributeNames)
                }
                is ConcreteFilter -> {
                    val nestedProperties = it.getFilterRequestProperties()
                    filterExpression += nestedProperties.filterExpression
                    expressionAttributeValues.putAll(nestedProperties.expressionAttributeValues)
                    expressionAttributeNames.putAll(nestedProperties.expressionAttributeNames)
                }
            }
        }

        val condition = filterQuery.first().value
        filterClosure(condition)

        filterQuery.drop(1).forEach {
            when (it.connectionToLeft) {
                FilterBooleanConnection.AND -> {
                    filterExpression += " AND "
                    filterClosure(it.value)
                }
                FilterBooleanConnection.OR -> {
                    filterExpression += " OR "
                    filterClosure(it.value)
                }
                null -> throw RuntimeException("Non head Filter without connection to left")
            }
        }

        return FilterRequestProperties(expressionAttributeValues, filterExpression, expressionAttributeNames)

    }
}