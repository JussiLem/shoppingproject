package shoppingproject.mutations

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import shoppingproject.dynamoDb
import shoppingproject.dynamodb.batch.DynamoBatchExecutor
import shoppingproject.mappers.PriceMapper
import shoppingproject.model.Product
import shoppingproject.utils.genericBadRequest

suspend fun PipelineContext<Unit, ApplicationCall>.postProduct() {
    try {
        val product = call.receive<Product>()
        application.log.debug("Product", product.toString())
        val amazonDynamoDB: AmazonDynamoDB = dynamoDb()
        val dynamoBatch: DynamoBatchExecutor<Product> = DynamoBatchExecutor(amazonDynamoDB)

        dynamoBatch.persist(
            listOf(Product(product.name, product.type, product.price)),
            PriceMapper(),
            "jussi-product"
        )
        call.respond(
            mapOf("OK" to true)
        )
    } catch (e: Exception) {
        application.log.error("Failed to save new product", e)
        genericBadRequest(e)
    }
}