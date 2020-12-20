package shoppingproject

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong
import io.ktor.serialization.*
import io.ktor.util.*
import shoppingproject.health.Health


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
        exception<Throwable> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(Health)

    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        generate { newRequestId() }
        verify { it.isNotEmpty() }
    }

    routing(Routing::generateRoutes)

}

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun dynamoDb(): AmazonDynamoDB {
    return AmazonDynamoDBAsyncClientBuilder.standard()
        .withCredentials(ProfileCredentialsProvider("bdd"))
        .withRegion(Regions.EU_WEST_1).build()
}

private val lastIncrement = AtomicLong()
private fun newRequestId(): String = "shoppingproject-id-${lastIncrement.incrementAndGet()}"

@KtorExperimentalAPI
val Application.envKind
    get() = environment.config.property("ktor.environment").getString()
