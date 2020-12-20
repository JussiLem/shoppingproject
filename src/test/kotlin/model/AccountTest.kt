package model

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import shoppingproject.model.Account
import shoppingproject.model.Privilege
import shoppingproject.model.Role
import shoppingproject.model.UserGroup

class AccountTest {
    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        Assertions.assertThatThrownBy {
            Account(
                "",
                "john.doe@domain.suffix",
                UserGroup(
                    "testGroup",
                    Role(
                        "TestRole",
                        listOf(Privilege.READ)
                    )
                )
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Name cannot be blank")
    }

    @Test
    fun `when email is invalid, throw IllegalArgEx`() {
        Assertions.assertThatThrownBy {
            Account(
                "Bruno", "invalidEmail",
                UserGroup(
                    "testGroup",
                    Role(
                        "TestRole",
                        listOf(Privilege.READ)
                    )
                )
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid email address")
    }

    @Test
    fun `when privilege is set for user, it should be read`() {
        Assertions.assertThat(
            Account(
                "Bruno", "email@email.com",
                UserGroup(
                    "testGroup",
                    Role(
                        "TestRole",
                        listOf(Privilege.READ)
                    )
                )
            ).userGroup.role.privilege.equals(Privilege.READ)
        )

    }
}