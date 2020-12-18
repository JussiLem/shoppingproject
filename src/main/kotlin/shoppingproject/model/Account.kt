package shoppingproject.model

import shoppingproject.utils.Patterns.emailRegex

data class Account(val name: String, val email: String) {

    init {
        validateName(name)
        validateEmail(email)
    }


}

fun validateEmail(email: String) {
    require(emailRegex.matcher(email).matches()) { "Invalid email address" }

}

fun validateName(name: String) {
    require(name.isNotBlank()) { "Name cannot be blank" }
}