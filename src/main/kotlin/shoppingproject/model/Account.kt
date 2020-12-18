package shoppingproject.model

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import shoppingproject.utils.Patterns.emailRegex

@DynamoDBTable(tableName = "jussi-account")
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