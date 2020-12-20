package shoppingproject.mutations

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import shoppingproject.dynamoDb
import shoppingproject.dynamodb.batch.DynamoBatchExecutor
import shoppingproject.mappers.AccountMapper
import shoppingproject.model.Account
import shoppingproject.utils.genericBadRequest

suspend fun PipelineContext<Unit, ApplicationCall>.postAccount() {
    try {
        val account = call.receive<Account>()
        application.log.debug("Account received", account.toString())
        val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
        val dynamoBatch: DynamoBatchExecutor<Account> = DynamoBatchExecutor(amazonDynamoDB)
        dynamoBatch.persist(
            listOf(Account(account.name, account.email, account.userGroup)),
            AccountMapper(),
            "jussi-account")
        call.respond(
            mapOf("OK" to true)
        )
    } catch (e: Exception) {
        application.log.error("Failed to register account, {}, requestId {}", e, call.callId)
        genericBadRequest(e)
    }
}