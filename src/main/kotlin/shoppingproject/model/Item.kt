package shoppingproject.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(val product: Product, var quantity: Int) {
    constructor(quantity: Int, product: Product) : this(product, quantity)

    val subtotal by lazy { product.price.times(quantity.toBigDecimal()) }

    init {
        require(quantity > 0) { "Quantity must be greater than 0" }
    }

    fun addMore(quantity: Int) = apply {
        require(quantity > 0) { "Quantity must be greater than 0" }
        this.quantity += quantity
    }

    fun updateTo(quantity: Int) = apply {
        require(quantity >= 0) { "Quantity must be equal or greater than 0" }
        this.quantity = quantity
    }
}
