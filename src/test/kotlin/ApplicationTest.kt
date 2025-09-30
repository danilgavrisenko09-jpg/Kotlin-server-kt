package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testGetAllProducts() = testApplication {
        application {
            configureApplication()
        }

        val response = client.get("/products")
        assertEquals(HttpStatusCode.OK, response.status)
        println("✅ GET /products - Status: ${response.status}")
    }

    @Test
    fun testGetProductById() = testApplication {
        application {
            configureApplication()
        }

        val response = client.get("/products/1")
        assertEquals(HttpStatusCode.OK, response.status)
        println("✅ GET /products/1 - Status: ${response.status}")
    }

    @Test
    fun testGetProductByIdNotFound() = testApplication {
        application {
            configureApplication()
        }

        val response = client.get("/products/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        println("✅ GET /products/999 - Status: ${response.status}")
    }

    @Test
    fun testCreateProduct() = testApplication {
        application {
            configureApplication()
        }

        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            setBody("""{"productName":"Test Product"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        println("✅ POST /products - Status: ${response.status}")
    }

    @Test
    fun testDeleteProduct() = testApplication {
        application {
            configureApplication()
        }

        val response = client.delete("/products/1")
        assertTrue(response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NotFound)
        println("✅ DELETE /products/1 - Status: ${response.status}")
    }
}