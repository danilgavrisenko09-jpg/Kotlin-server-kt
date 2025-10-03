import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import org.slf4j.event.Level

// Модели данных
@Serializable
data class Task(val id: Int, val title: String, val completed: Boolean = false)

@Serializable
data class CreateTaskRequest(val title: String)

@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: List<Task>? = null
)

// Хранилище в памяти
val taskStorage = mutableListOf(
    Task(1, "Learn Ktor", true),
    Task(2, "Build API", false),
    Task(3, "Write tests", false)
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    // Логирование
    install(CallLogging) {
        level = Level.INFO
    }

    // JSON
    install(ContentNegotiation) {
        json()
    }

    // Обработка ошибок
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError,
                ApiResponse(false, "Error: ${cause.message}"))
        }
        exception<NotFoundException> { call, _ ->
            call.respond(HttpStatusCode.NotFound,
                ApiResponse(false, "Task not found"))
        }
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,
                ApiResponse(false, "Bad request: ${cause.message}"))
        }
    }

    // Роуты
    routing {
        get("/") {
            call.respondText("Task API - Visit /docs for documentation")
        }

        // Документация API
        get("/docs") {
            val docs = """
                TASK API DOCUMENTATION
                
                ENDPOINTS:
                GET    /tasks                 - Get all tasks
                GET    /tasks?filter=completed - Get completed tasks  
                GET    /tasks?filter=active    - Get active tasks
                GET    /tasks/{id}            - Get task by ID
                POST   /tasks                 - Create new task
                DELETE /tasks/{id}            - Delete task
                PATCH  /tasks/{id}/toggle     - Toggle task status
                
                EXAMPLES:
                Create task: POST /tasks with JSON: {"title": "Task name"}
                Get task: GET /tasks/1
                Delete task: DELETE /tasks/1
            """.trimIndent()
            call.respondText(docs)
        }

        // GET /tasks - получить все задачи
        get("/tasks") {
            val filter = call.request.queryParameters["filter"]
            val tasks = when (filter) {
                "completed" -> taskStorage.filter { it.completed }
                "active" -> taskStorage.filter { !it.completed }
                else -> taskStorage
            }
            call.respond(ApiResponse(true, "Tasks retrieved successfully", tasks))
        }

        // GET /tasks/{id} - получить задачу по ID
        get("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw BadRequestException("Invalid ID")

            val task = taskStorage.find { it.id == id }
                ?: throw NotFoundException()

            call.respond(ApiResponse(true, "Task found", listOf(task)))
        }

        // POST /tasks - создать новую задачу
        post("/tasks") {
            val request = call.receive<CreateTaskRequest>()

            if (request.title.isBlank()) {
                throw BadRequestException("Title cannot be empty")
            }

            val newId = (taskStorage.maxOfOrNull { it.id } ?: 0) + 1
            val newTask = Task(newId, request.title)

            taskStorage.add(newTask)
            call.respond(HttpStatusCode.Created,
                ApiResponse(true, "Task created successfully", listOf(newTask)))
        }

        // DELETE /tasks/{id}
        delete("/tasks/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw BadRequestException("Invalid ID")

            val task = taskStorage.find { it.id == id }
                ?: throw NotFoundException()

            taskStorage.remove(task)
            call.respond(ApiResponse(true, "Task deleted successfully", listOf(task)))
        }

        // PATCH /tasks/{id}/toggle
        patch("/tasks/{id}/toggle") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw BadRequestException("Invalid ID")

            val task = taskStorage.find { it.id == id }
                ?: throw NotFoundException()

            val updatedTask = task.copy(completed = !task.completed)
            taskStorage[taskStorage.indexOf(task)] = updatedTask

            call.respond(ApiResponse(true, "Task status updated", listOf(updatedTask)))
        }
    }
}
