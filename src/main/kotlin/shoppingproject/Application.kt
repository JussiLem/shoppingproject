package shoppingproject

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.respondHtml
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.*
import kotlinx.html.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import shoppingproject.dynamodb.batch.DynamoBatchExecutor
import shoppingproject.dynamodb.dsl.*
import shoppingproject.mappers.AccountMapper
import shoppingproject.mappers.PriceMapper
import shoppingproject.model.Account
import shoppingproject.model.Product
import shoppingproject.model.validateEmail
import shoppingproject.model.validateName
import java.util.concurrent.atomic.AtomicLong
import kotlin.IllegalArgumentException
import com.amazonaws.services.dynamodbv2.document.ItemUtils


fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
    }
}

object MyLogger {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)
}

@ExperimentalCoroutinesApi
val myContext = newSingleThreadContext("MyOwnThread")

@ExperimentalCoroutinesApi
fun Application.module() {
    // This adds automatically Date and Server headers to each response, and would allow you to configure
    // additional headers served to each response.
    install(DefaultHeaders)

    // This uses use the logger to log every call (request/response)
    install(CallLogging) {
        callIdMdc("request-id")
    }

    install(StatusPages) {
        exception<InvalidCredentialsException> { exception ->
            call.respond(HttpStatusCode.Unauthorized, mapOf("OK" to false, "error" to (exception.message ?: "")))
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }

    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        generate { newRequestId() }
        verify { it.isNotEmpty() }
    }

    routing {

        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }

        get("/account") {
            val deferred = async(myContext) {
                call.respondAccountQuery()
            }
            deferred.await()
        }

        post("/account") {
            try {
                val post = call.receive<Account>()
                application.log.debug("Account received", post.toString())
                val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
                val dynamoBatch: DynamoBatchExecutor<Account> = DynamoBatchExecutor(amazonDynamoDB)
                dynamoBatch.persist(listOf(Account(post.name, post.email)), AccountMapper(), "jussi-account")
                call.respond(mapOf("OK" to true))
            } catch (e: Exception) {
                application.log.error("Failed to register account", e)
                call.respond(HttpStatusCode.BadRequest, "Problem creating Account")
            }
        }
        post("/product") {
            try {
                val product = call.receive<Product>()
                application.log.debug("Product", product.toString())
                val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
                val dynamoBatch: DynamoBatchExecutor<Product> = DynamoBatchExecutor(amazonDynamoDB)

                dynamoBatch.persist(
                    listOf(Product(product.name, product.type, product._price)),
                    PriceMapper(),
                    "jussi-product"
                )
                call.respond(mapOf("OK" to true))
            } catch (e: Exception) {
                application.log.error("Failed to register account", e)
                call.respond(HttpStatusCode.BadRequest, "Problem creating Account")
            }
        }
    }

}

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun dynamoDb(): AmazonDynamoDB {
    return AmazonDynamoDBAsyncClientBuilder.standard()
        .withCredentials(ProfileCredentialsProvider("bdd"))
        .withRegion(Regions.EU_WEST_1).build()
}

private val lastIncrement = AtomicLong()
private fun newRequestId(): String = "shoppingproject-id-${lastIncrement.incrementAndGet()}"

private suspend fun ApplicationCall.respondAccountQuery() {
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
        while (result.hasNext()) {
            application.log.info(ItemUtils.toItem(result.next()).toJSON())
        }





    } catch (e: IllegalArgumentException) {
        application.log.error("Error while parsing parameters", e)
        respond(
            HttpStatusCode.BadRequest,
            mapOf(
                "ResponseCode" to HttpStatusCode.BadRequest.value,
                "ValidationError" to e.message.toString(),
                "CallId" to callId.toString()
            )
        )
    }
}
