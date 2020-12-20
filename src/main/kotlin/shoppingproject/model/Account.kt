package shoppingproject.model

import shoppingproject.utils.Patterns.emailRegex
import kotlinx.serialization.Serializable

@Serializable
data class Account(val name: String, val email: String, val userGroup: UserGroup) {

    init {
        require(emailRegex.matcher(email).matches()) { "Invalid email address" }
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(userGroup.toString().isNotBlank()) { "user group cannot be blank" }
    }
}
