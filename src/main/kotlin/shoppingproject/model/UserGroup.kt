package shoppingproject.model

import kotlinx.serialization.Serializable

@Serializable
data class UserGroup(val name: String, val role: Role) {

    init {
        require(name.isNotBlank()) { "UserGroup name cannot be blank" }
        require(role.toString().isNotBlank()) { "There needs to be one role set" }
    }
}