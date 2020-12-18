package shoppingproject.model

import kotlinx.serialization.Serializable

@Serializable
data class Invoice(private val order: Order) {

    val items: List<Item> = order.items
}
