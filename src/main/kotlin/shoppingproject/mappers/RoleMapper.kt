package shoppingproject.mappers

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import shoppingproject.dynamodb.batch.DynamoMapper
import shoppingproject.model.Role

class RoleMapper : DynamoMapper<Role> {
    override fun mapToDynamoItem(role: Role): Map<String, AttributeValue> {
        return mapOf(
            Pair("name", AttributeValue(role.name)),
            Pair("privilege", AttributeValue().withSS(role.privilege.toString())),
        )
    }
}