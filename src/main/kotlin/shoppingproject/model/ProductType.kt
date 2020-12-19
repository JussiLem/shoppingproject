package shoppingproject.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProductType {
    PHYSICAL,
    PHYSICAL_TAX_FREE,
    DIGITAL,
    SUBSCRIPTION
}