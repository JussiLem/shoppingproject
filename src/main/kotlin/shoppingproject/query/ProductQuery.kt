package shoppingproject.query

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import shoppingproject.ShoppingProjectException
import shoppingproject.dynamoDb
import shoppingproject.dynamodb.dsl.DynamoDSL
import shoppingproject.dynamodb.dsl.eq
import shoppingproject.dynamodb.dsl.hashKey
import shoppingproject.dynamodb.dsl.sortKey
import shoppingproject.model.*

suspend fun ApplicationCall.productQuery() {
    try {
        application.log.info("Looking for product")
        val name: String? = request.queryParameters["name"]
        val type: String? = request.queryParameters["type"]

        application.log.debug("Params: {}, {}, {}", name, type, callId.toString())
        val amazonDynamoDB: AmazonDynamoDB = dynamoDb()

        val result = DynamoDSL(amazonDynamoDB).query("jussi-product") {
            hashKey("name") {
                eq(name!!)
            }
            sortKey("type") {
                eq(type!!)
            }
        }
//        while (result.hasNext()) {
//            application.log.debug(result.next().toString())
//        }
        val json = ItemUtils.toItem(result.next()).toJSON()
        application.log.debug(json)
        val product = Json.decodeFromString<Product>(json)
        respond(
            HttpStatusCode.OK,
            mapOf(
                "CallId" to callId.toString(),
                "Name" to product.name,
                "Type" to product.type.toString(),
                "Price" to product.price.toString()
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
    } catch (e: ShoppingProjectException) {
        application.log.error("Error while parsing parameters", e)
        respond(
            HttpStatusCode.BadRequest,
            mapOf(
                "ValidationError" to e.message.toString(),
                "CallId" to callId.toString()
            )
        )
    } catch (e: Exception) {
        application.log.error("Unexpected things happened", e)
        respond(
            HttpStatusCode.ServiceUnavailable,
            mapOf(
                "ValidationError" to "Error",
                "CallId" to callId.toString()
            )
        )
    }
}