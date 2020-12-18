package shoppingproject.dynamodb.dsl.filters

import shoppingproject.dynamodb.dsl.DynamoDSLMarker
import shoppingproject.dynamodb.dsl.QueryIteratorBuilder
import shoppingproject.dynamodb.dsl.conditions.*


@DynamoDSLMarker
fun QueryIteratorBuilder.filtering(block: RootFilterBuilder.() -> Unit) {
    filtering = RootFilterBuilder().apply(block).build()
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.eq(value: Any){
    comparator = Equals(value)
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.noteq(value: Any){
    comparator = NotEquals(value)
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.gt(value: Any){
    comparator = GreaterThan(value)
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.lt(value: Any){
    comparator = LessThan(value)
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.gteq(value: Any){
    comparator = GreaterThanOrEquals(value)
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.lteq(value: Any){
    comparator = LessThanOrEquals(value)
}

@DynamoDSLMarker
fun ConcreteFilterBuilder.inList(value: List<Any>){
    comparator = InList(value)
}

@DynamoDSLMarker
fun RootFilterBuilder.attribute(value: String, block: ConcreteFilterBuilder.() -> Unit) : RootFilterBuilder{

    return if (this.filterQueries.isEmpty()){
        val concreteFilter = ConcreteFilterBuilder().apply(block)
        concreteFilter.dynamoFunction = Attribute(value)
        this.filterQueries.add(FilterConnection(concreteFilter.build(), null))
        this
    }
    else {
        val concreteFilter = ConcreteFilterBuilder().apply(block)
        concreteFilter.dynamoFunction = Attribute(value)
        this.currentFilter = concreteFilter.build()
        this
    }
}

@DynamoDSLMarker
fun RootFilterBuilder.attributeExists(value: String) : RootFilterBuilder{
    this.currentFilter = ConcreteFilter(AttributeExists(value))
    return this
}
