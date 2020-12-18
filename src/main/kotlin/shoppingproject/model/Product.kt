package shoppingproject.model

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped
import java.math.BigDecimal
import java.math.RoundingMode

data class Product(
    val name: String,
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.S) val type: ProductType,
    val _price: Double) {
    val price: BigDecimal by lazy { BigDecimal(_price).setScale(2, RoundingMode.HALF_UP) }

    init {
        require(name.isNotBlank()) { "Product name must not be blank" }
        require(_price > 0.0) { "Produce price must be greaterThan 0" }
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
