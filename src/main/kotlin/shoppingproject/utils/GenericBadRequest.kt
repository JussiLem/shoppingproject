package shoppingproject.utils

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.genericBadRequest(
    e: Exception
) {
    call.respond(
        HttpStatusCode.BadRequest,
        mapOf(
            "ValidationError" to e.message.toString(),
            "CallId" to call.callId.toString()
        )
    )
}