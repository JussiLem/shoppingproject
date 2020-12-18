package shoppingproject.model

import java.math.BigDecimal

data class PhysicalOrder(override val items: List<Item>, override val account: Account) : Order {

    override val feesAndDiscounts = HashMap<String, BigDecimal>()
    override lateinit var paymentMethod: PaymentMethod
    override lateinit var status: OrderStatus

    override val type: OrderType = OrderType.PHYSICAL
    lateinit var shippingAddress: Address

    val parcels: () -> List<Parcel> = { Parcel.breakdown(items, shippingAddress) }

    init {
        require(items.none { it.product.type != ProductType.PHYSICAL && it.product.type != ProductType.PHYSICAL_TAX_FREE }) {
            "Physical order may only contain physical items"
        }
    }

    fun withShippingAddress(address: Address) = apply { this.shippingAddress = address }

    override fun withPaymentMethod(paymentMethod: PaymentMethod) = apply {
        super.withPaymentMethod(paymentMethod)
    }

    override fun place() = apply {
        require(this::shippingAddress.isInitialized) {
            "Shipping Address must be informed for Orders with physical delivery"
        }
        require(this::paymentMethod.isInitialized) {
            "A Payment method must be informed to place the Order"
        }
        super.place()
        this.feesAndDiscounts["shippingAndHandling"] = Parcel.shippingCostsOf(parcels())
        this.status = OrderStatus.PENDING
    }

    override fun fulfill() = apply {
        super.fulfill()
        this.status = OrderStatus.SHIPPED
    }

    override fun complete() = apply {
        super.complete()
        this.status = OrderStatus.DELIVERED
    }
}
