package shoppingproject

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import shoppingproject.dynamodb.dsl.DynamoDSL
import shoppingproject.dynamodb.dsl.eq
import shoppingproject.dynamodb.dsl.hashKey
import shoppingproject.dynamodb.dsl.sortKey
import shoppingproject.model.Account
import shoppingproject.model.validateEmail
import shoppingproject.model.validateName

suspend fun ApplicationCall.respondAccountQuery() {

    try {
        val name: String? = request.queryParameters["name"]
        val email: String? = request.queryParameters["email"]
        validateName(name!!)
        validateEmail(email!!)
        val amazonDynamoDB: AmazonDynamoDB = dynamoDb()

        val result = DynamoDSL(amazonDynamoDB).query("jussi-account") {
            hashKey("name") {
                eq(name)
            }
            sortKey("email") {
                eq(email)
            }
        }

        val json = ItemUtils.toItem(result.next()).toJSON()
        val account = Json.decodeFromString<Account>(json)
        respond(
            HttpStatusCode.OK,
            mapOf(
                "CallId" to callId.toString(),
                "Name" to account.name,
                "Email" to account.email
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