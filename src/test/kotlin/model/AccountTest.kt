package model

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import shoppingproject.model.Account

class AccountTest {
    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        Assertions.assertThatThrownBy { Account("", "john.doe@domain.suffix") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Name cannot be blank")
    }

    @Test
    fun `when email is invalid, throw IllegalArgEx`() {
        Assertions.assertThatThrownBy { Account("Bruno", "invalidEmail") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid email address")
    }
}