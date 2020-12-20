package shoppingproject

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import shoppingproject.mutations.postAccount
import shoppingproject.mutations.postProduct
import shoppingproject.query.productQuery
import shoppingproject.query.respondAccountQuery

@ExperimentalCoroutinesApi
fun Routing.generateRoutes() {
    val myContext = newSingleThreadContext("MyOwnThread")

    get("/account") {
        val deferred = async(myContext) {
            call.respondAccountQuery()
        }
        deferred.await()
    }

    get("/product") {
        val deferred = async(myContext) {
            call.productQuery()
        }
        deferred.await()
    }

    post("/account") {
        val deferred = async(myContext) {
            postAccount()
        }
        deferred.await()
    }
    post("/product") {
        val deferred = async(myContext) {
            postProduct()
        }
        deferred.await()
    }
}