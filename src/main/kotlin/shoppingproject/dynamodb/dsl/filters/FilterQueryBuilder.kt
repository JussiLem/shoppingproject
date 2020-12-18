package shoppingproject.dynamodb.dsl.filters

interface FilterQueryBuilder {
    fun build() : FilterQuery
}