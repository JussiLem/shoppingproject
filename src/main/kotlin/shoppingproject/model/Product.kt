package shoppingproject.model

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val name: String,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S) val type: ProductType,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.N) val price: Double) {
    val priceInBigDecimal: BigDecimal by lazy { BigDecimal(price).setScale(2, RoundingMode.HALF_UP) }

    init {
        require(name.isNotBlank()) { "Product name must not be blank" }
        require(price > 0.0) { "Produce price must be greaterThan 0" }
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Product -> (this.name == other.name) && (this.type == other.type)
            else -> false
        }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
