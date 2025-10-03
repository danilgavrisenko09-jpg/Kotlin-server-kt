import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application { module() }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Task API"))
        }
    }

    @Test
    fun testDocs() = testApplication {
        application { module() }
        client.get("/docs").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("ENDPOINTS"))
        }
    }

    @Test
    fun testGetAllTasks() = testApplication {
        application { module() }
        client.get("/tasks").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Learn Ktor"))
        }
    }

    @Test
    fun testGetTaskById() = testApplication {
        application { module() }
        client.get("/tasks/1").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Learn Ktor"))
        }
    }

    @Test
    fun testGetTaskNotFound() = testApplication {
        application { module() }
        client.get("/tasks/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }

    @Test
    fun testCreateTask() = testApplication {
        application { module() }
        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"title": "New Task"}""")
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("New Task"))
    }

    @Test
    fun testDeleteTask() = testApplication {
        application { module() }
        client.delete("/tasks/2").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Build API"))
        }
    }

    @Test
    fun testToggleTask() = testApplication {
        application { module() }
        client.patch("/tasks/3/toggle").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Write tests"))
        }
    }

    @Test
    fun testFilterTasks() = testApplication {
        application { module() }
        client.get("/tasks?filter=completed").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("Learn Ktor"))
        }
    }
}
