package shoppingproject.model

data class Invoice(private val order: Order) {

    val items: List<Item> = order.items
}
