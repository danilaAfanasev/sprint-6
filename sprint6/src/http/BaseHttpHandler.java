package http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.Gson;
import controllers.Managers;
import controllers.TaskManager;

public class BaseHttpHandler {
    public TaskManager taskManager;
    private Charset utf = StandardCharsets.UTF_8;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    Gson gson = Managers.createGson();

    public void sendText(HttpExchange ex, String response, int code) throws IOException {
        ex.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        ex.sendResponseHeaders(code, 0);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(response.getBytes(utf));
        }
    }

    public void sendIncorrectId(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        ex.sendResponseHeaders(400, 0);
        try (OutputStream os = ex.getResponseBody()) {
            os.write("id введен некорректно".getBytes(utf));
        }
    }

    public void sendIncorrectMethod(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        ex.sendResponseHeaders(405, 0);
        try (OutputStream os = ex.getResponseBody()) {
            os.write("Обработка метода не предусмотрена".getBytes(utf));
        }
    }

    public Optional<Integer> getId(HttpExchange ex) {
        try {
            String[] splitPath = ex.getRequestURI().getPath().split("/");
            return Optional.of(Integer.parseInt(splitPath[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public String bodyToString(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), utf);
    }

    public Gson getGson() {
        return gson;
    }
}