package shoppingproject.dynamodb.dsl.filters

import shoppingproject.dynamodb.dsl.DynamoDSLMarker

@DynamoDSLMarker
class RootFilterBuilder: FilterQueryBuilder {
    var currentFilter: FilterQuery? = null

    var filterQueries: MutableList<FilterConnection> = mutableListOf()
    override fun build(): RootFilter = RootFilter(filterQueries)

    //Following 2 method are equivalent to bracketed conditions
    @DynamoDSLMarker
    infix fun and(block: RootFilterBuilder.() -> Unit): RootFilterBuilder {
        val value = RootFilterBuilder().apply(block)
        val connectionToLeft = FilterBooleanConnection.AND
        filterQueries.add(FilterConnection(value.build(), connectionToLeft))
        return this
    }

    @DynamoDSLMarker
    infix fun or(block: RootFilterBuilder.() -> Unit): RootFilterBuilder {
        val value = RootFilterBuilder().apply(block)
        val connectionToLeft = FilterBooleanConnection.OR
        filterQueries.add(FilterConnection(value.build(), connectionToLeft))
        return this
    }

    @DynamoDSLMarker
    infix fun and(value: RootFilterBuilder): RootFilterBuilder {
        filterQueries.add(FilterConnection(this.currentFilter!!, FilterBooleanConnection.AND))
        return this
    }

    @DynamoDSLMarker
    infix fun or(value: RootFilterBuilder): RootFilterBuilder {
        filterQueries.add(FilterConnection(this.currentFilter!!, FilterBooleanConnection.OR))
        return this
    }
}