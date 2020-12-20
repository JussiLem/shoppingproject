package shoppingproject.model

import kotlinx.serialization.Serializable

@Serializable
data class Role(val name: String, val privilege: List<Privilege>) {

    init {
        require(name.isNotBlank()) { "Role name cannot be blank" }
        require(privilege.isNotEmpty()) { "There needs to be at least one privilege" }
    }
}
