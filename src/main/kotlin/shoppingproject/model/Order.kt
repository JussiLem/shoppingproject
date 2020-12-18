package shoppingproject.model

import java.math.BigDecimal
import java.math.RoundingMode

interface Order {
    val items: List<Item>
    val feesAndDiscounts: Map<String, BigDecimal>
    val account: Account

    var paymentMethod: PaymentMethod
    var status: OrderStatus
    val type: OrderType

    fun subtotal(): BigDecimal {
        return items
            .map(Item::subtotal)
            .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
            .setScale(2, RoundingMode.UP)
    }

    fun feesAndDiscounts(): BigDecimal {
        return feesAndDiscounts
            .values
            .fold(BigDecimal.ZERO) { acc, value -> acc.plus(value) }
            .setScale(2, RoundingMode.UP)
    }

    fun grandTotal(): BigDecimal {
        return subtotal().plus(feesAndDiscounts())
    }

    fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        this.paymentMethod = paymentMethod
    }

    fun place() = apply {
        require(items.isNotEmpty()) {
            "Must be at least one item to place the Order"
        }
    }

    fun pay() = apply {
        check(status.code >= OrderStatus.PENDING.code) {
            "Order must be placed before it can be payed"
        }
        check(status.code < OrderStatus.NOT_SHIPPED.code) {
            "Order Payment has been processed already"
        }
    }

    fun invoice(): Invoice {
        check((status.code < OrderStatus.NOT_SHIPPED.code).not()) {
            "Invoice can only be generated after payment is complete"
        }
        return Invoice(this)
    }


    fun fulfill() = apply {
        check(status.code >= OrderStatus.NOT_SHIPPED.code) {
            "Order must be placed and payed before it can be fulfilled"
        }
        check(status.code < OrderStatus.SHIPPED.code) {
            "Order Fulfillment has been processed already"
        }
    }

    fun complete() = apply {
        check(status.code >= OrderStatus.SHIPPED.code) {
            "Order must have been shipped/sent and confirmed, before it can be completed"
        }
        check(status.code < OrderStatus.DELIVERED.code) {
            "Order has been delivered already"
        }
    }

}