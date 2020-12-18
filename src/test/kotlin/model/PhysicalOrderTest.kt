package model

import shoppingproject.model.ProductType.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import shoppingproject.model.*
import java.time.YearMonth

class PhysicalOrderTest {
    private val billingAddress: Address by lazy {
        Address.builder
            .withCountry("Brazil")
            .withCity("Sao Paulo")
            .withState("SP")
            .withZipCode("01000-000")
            .withStreetAddress("Av Paulista, 1000")
            .build()
    }
    private val paymentMethod: PaymentMethod by lazy {
        CreditCard("JOHN DOE", "123.456.789-00", 123, YearMonth.of(2027, 11), billingAddress)
    }
    private val account by lazy { Account("John Doe", "john.doe@domain.suffix") }
    private val shippingAddress = billingAddress

    private lateinit var physicalItems: List<Item>
    private lateinit var physicalTaxFreeItems: List<Item>

    @BeforeEach
    fun setup() {
        val console = Product("PS4 Slim 1TB", PHYSICAL, 1899.00)
        val chair = Product("PDP Chair", PHYSICAL, 399.00)
        val book = Product("Cracking the Code Interview", PHYSICAL_TAX_FREE, 219.57)
        val anotherBook =
            Product("The Hitchhiker's Guide to the Galaxy", PHYSICAL_TAX_FREE, 120.00)

        physicalItems = listOf(Item(console, 1), Item(chair, 2))
        physicalTaxFreeItems = listOf(Item(book, 2), Item(anotherBook, 1))
    }

    @Test
    fun `when creating a Physical Order, there must be only Physical items in the list`() {
        val product = Product("digital product", DIGITAL, 1.99)
        assertThatThrownBy { PhysicalOrder(listOf(Item(product, 1)), account) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Physical order may only contain physical items")
    }

    @Test
    fun `when placing a PhysicalOrder, there must be at least one item in the list`() {
        val order: PhysicalOrder =
            PhysicalOrder(listOf(), account)
                .withShippingAddress(shippingAddress)
                .withPaymentMethod(paymentMethod)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There must be at least one item to place the Order")
    }

    @Test
    fun `when placing a PhysicalOrder, a paymentMethod must be informed`() {
        val order: PhysicalOrder = PhysicalOrder(physicalItems, account)
            .withShippingAddress(shippingAddress)

        assertThatThrownBy { order.place() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("A Payment method must be informed to place the Order")
    }

}