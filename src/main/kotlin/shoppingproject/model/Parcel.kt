package shoppingproject.model

import shoppingproject.model.ProductType.*
import java.math.BigDecimal
import java.math.RoundingMode

import kotlinx.serialization.Serializable

@Serializable
data class Parcel(
    val items: List<Item>,
    val shippingAddress: Address,
    val shippingLabel: ShippingLabel
) {

    companion object {

        fun breakdown(items: List<Item>, shippingAddress: Address): List<Parcel> {
            return items.groupBy { it.product.type }.map { (productType, items) ->
                when (productType) {
                    PHYSICAL -> Parcel(items, shippingAddress, ShippingLabel.DEFAULT)
                    PHYSICAL_TAX_FREE -> Parcel(items, shippingAddress, ShippingLabel.TAX_FREE)
                    DIGITAL, SUBSCRIPTION -> throw IllegalStateException("Unmapped ProductType, no corresponding Shipping Label")
                }
            }
        }

        fun shippingCostsOf(parcels: List<Parcel>): BigDecimal {
            return parcels
                .map { it.shippingAndHandlingCosts() }
                .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
                .setScale(2, RoundingMode.HALF_UP)
        }
    }

    fun shippingAndHandlingCosts(): BigDecimal {
        // TODO("compute logistics costs based on size, weight and shippingAddress")
        return BigDecimal.TEN
    }
}
