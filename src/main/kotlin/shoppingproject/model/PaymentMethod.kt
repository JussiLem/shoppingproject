package shoppingproject.model

import java.math.BigDecimal

interface PaymentMethod {

    val billingAddress: Address

    fun charge(amount: BigDecimal): Boolean
}