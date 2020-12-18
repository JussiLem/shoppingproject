package shoppingproject.model

import java.math.BigDecimal
import java.time.YearMonth

data class CreditCard(
    val nameOnCard: String,
    val number: String,
    val securityCode: Int,
    val expiresAt: YearMonth,
    override val billingAddress: Address
) : PaymentMethod {

    override fun charge(amount: BigDecimal): Boolean {
        TODO("attempt transaction with payment broker")
    }
}
