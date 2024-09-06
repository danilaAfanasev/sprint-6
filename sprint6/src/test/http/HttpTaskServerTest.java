package test.http;

import com.google.gson.Gson;
import model.Status;
import http.BaseHttpHandler;
import http.HttpTaskServer;
import controllers.InMemoryTaskManager;
import controllers.TaskManager;
import org.junit.jupiter.api.*;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    public TaskManager taskManager = new InMemoryTaskManager();
    public BaseHttpHandler handler = new BaseHttpHandler(taskManager);
    public HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    public Gson gson = handler.getGson();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskServer.createHttpServer();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    @DisplayName("Проверить добавление задачи")
    public void testAddTask() throws IOException, InterruptedException {

        Task task = new Task("Задача", "Описание",1,
                Status.NEW,LocalDateTime.now(), 1000);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    @DisplayName("Проверить удаление задачи")
    public void testDeleteTask() throws IOException, InterruptedException {

        Task task = new Task("Задача", "Описание", 1,
                Status.NEW, LocalDateTime.now(), 1000);

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, deleteResponse.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Задача не удалена");
    }

    @Test
    @DisplayName("Проверить обновление задачи")
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание 1", 1,
                Status.NEW,LocalDateTime.parse("01.01.24 00:00", formatter), 1000);

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)) //добавляет тело запроса в запрос
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task updatedTask = new Task("Задача 1.1", "Описание 1.1", 1,
                Status.DONE,LocalDateTime.parse("02.01.24 00:00", formatter), 1000);
        String updatedTaskJson = gson.toJson(updatedTask);
        URI urlForUpdate = URI.create("http://localhost:8080/tasks");
        HttpRequest requestForUpdate = HttpRequest.newBuilder()
                .uri(urlForUpdate)
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();
        HttpResponse<String> responseUpdate = client.send(requestForUpdate, HttpResponse.BodyHandlers.ofString());
        List<Task> map = taskManager.getListOfTasks();
        assertEquals(201, responseUpdate.statusCode());
        assertEquals("Задача с id 1 обновлена", responseUpdate.body());
        assertEquals("Задача 1.1", Optional.of(taskManager.getListOfTasks().get(0).getName()).orElse("Имя отсутствует"));
    }
}