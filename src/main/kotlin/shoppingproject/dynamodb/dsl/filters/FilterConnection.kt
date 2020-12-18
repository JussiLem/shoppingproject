package shoppingproject.dynamodb.dsl.filters

//Represents a connector and an individual condition 'AND X' , 'OR (Y AND Z)' , etc
class FilterConnection(val value: FilterQuery,
                       val connectionToLeft : FilterBooleanConnection?) //Ie last in chain won't have a right connection