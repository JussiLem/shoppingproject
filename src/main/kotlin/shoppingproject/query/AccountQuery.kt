package shoppingproject.query

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import shoppingproject.dynamoDb
import shoppingproject.dynamodb.dsl.DynamoDSL
import shoppingproject.dynamodb.dsl.eq
import shoppingproject.dynamodb.dsl.hashKey
import shoppingproject.dynamodb.dsl.sortKey
import shoppingproject.model.Account

suspend fun ApplicationCall.respondAccountQuery() {

    try {
        val name: String? = request.queryParameters["name"]
        val email: String? = request.queryParameters["email"]
        val account = Account(name!!, email!!)
        val amazonDynamoDB: AmazonDynamoDB = dynamoDb()

        val result = DynamoDSL(amazonDynamoDB).query("jussi-account") {
            hashKey("name") {
                eq(account.name)
            }
            sortKey("email") {
                eq(account.email)
            }
        }

        val json = ItemUtils.toItem(result.next()).toJSON()
        val foundAccount = Json.decodeFromString<Account>(json)
        respond(
            HttpStatusCode.OK,
            mapOf(
                "CallId" to callId.toString(),
                "Name" to foundAccount.name,
                "Email" to foundAccount.email
            )
        )

    } catch (e: IllegalArgumentException) {
        application.log.error("Error while parsing parameters", e)
        respond(
            HttpStatusCode.BadRequest,
            mapOf(
                "ValidationError" to e.message.toString(),
                "CallId" to callId.toString()
            )
        )
    }
}