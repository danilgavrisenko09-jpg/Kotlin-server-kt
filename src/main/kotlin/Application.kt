package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import io.ktor.http.*


@Serializable
data class Product(val productId: Int, val productName: String)

@Serializable
data class CreateProductRequest(val productName: String)


class ProductInventory {
    private val productCollection = mutableListOf(
        Product(1, "First product"),
        Product(2, "Second product")
    )

    fun getAllProducts(): List<Product> = productCollection.toList()

    fun getProductById(productId: Int): Product? =
        productCollection.find { it.productId == productId }

    fun addNewProduct(productName: String): Product {
        val newProductId = (productCollection.maxOfOrNull { it.productId } ?: 0) + 1
        val newProduct = Product(newProductId, productName)
        productCollection.add(newProduct)
        return newProduct
    }

    fun removeProduct(productId: Int): Boolean =
        productCollection.removeIf { it.productId == productId }
}


class ProductHandler(private val inventory: ProductInventory) {
    suspend fun handleGetAllProducts(call: ApplicationCall) {
        call.respond(inventory.getAllProducts())
    }

    suspend fun handleGetProductById(call: ApplicationCall) {
        val productId = call.parameters["productId"]?.toIntOrNull()

        if (productId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid product ID")
            return
        }

        val product = inventory.getProductById(productId)
        if (product == null) {
            call.respond(HttpStatusCode.NotFound, "Product not found")
        } else {
            call.respond(product)
        }
    }

    suspend fun handleCreateProduct(call: ApplicationCall) {
        try {
            val request = call.receive<CreateProductRequest>()

            if (request.productName.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Product name cannot be empty")
                return
            }

            val newProduct = inventory.addNewProduct(request.productName)
            call.respond(HttpStatusCode.Created, newProduct)

        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    suspend fun handleDeleteProduct(call: ApplicationCall) {
        val productId = call.parameters["productId"]?.toIntOrNull()

        if (productId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid product ID")
            return
        }

        val removed = inventory.removeProduct(productId)
        if (removed) {
            call.respond(HttpStatusCode.OK, "Product deleted successfully")
        } else {
            call.respond(HttpStatusCode.NotFound, "Product not found")
        }
    }
}


fun Application.configureProductRoutes() {
    val inventory = ProductInventory()
    val handler = ProductHandler(inventory)

    routing {
        route("/products") {
            get {
                handler.handleGetAllProducts(call)
            }

            get("/{productId}") {
                handler.handleGetProductById(call)
            }

            post {
                handler.handleCreateProduct(call)
            }

            delete("/{productId}") {
                handler.handleDeleteProduct(call)
            }
        }
    }
}


fun Application.configureApplication() {
    install(ContentNegotiation) {
        json()
    }
    configureProductRoutes()
}


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureApplication()
    }.start(wait = true)
}