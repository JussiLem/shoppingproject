package shoppingproject.mutations

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import shoppingproject.dynamoDb
import shoppingproject.dynamodb.batch.DynamoBatchExecutor
import shoppingproject.mappers.RoleMapper
import shoppingproject.model.Role
import shoppingproject.utils.genericBadRequest

suspend fun PipelineContext<Unit, ApplicationCall>.postRole() {
    try {
        val role = call.receive<Role>()
        application.log.debug("Product", role.toString())
        val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
        val dynamoBatch: DynamoBatchExecutor<Role> = DynamoBatchExecutor(amazonDynamoDB)

        dynamoBatch.persist(
            listOf(Role(role.name, role.privilege)),
            RoleMapper(),
            "jussi-role"
        )
        call.respond(
            mapOf("OK" to true)
        )
    } catch (e: Exception) {
        application.log.error("Failed to save new role", e)
        genericBadRequest(e)
    }
}