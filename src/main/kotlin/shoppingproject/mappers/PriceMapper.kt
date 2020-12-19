package shoppingproject.mappers

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import shoppingproject.dynamodb.batch.DynamoMapper
import shoppingproject.model.Product

class PriceMapper : DynamoMapper<Product> {
    override fun mapToDynamoItem(product: Product): Map<String, AttributeValue> {
        return mapOf(
            Pair("name", AttributeValue(product.name)),
            Pair("type", AttributeValue().withS(product.type.toString())),
            Pair("price", AttributeValue().withN(product.priceInBigDecimal.toString())),
        )
    }
}