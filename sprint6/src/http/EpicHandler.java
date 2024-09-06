package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import exceptions.ManagerSaveException;
import model.Epic;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange ex) {
        String method = ex.getRequestMethod();
        String[] split = ex.getRequestURI().getPath().split("/");
        try {
            switch (method) {
                case "GET":
                    if (split.length == 2) {
                        handleGetEpicsResponse(ex);
                    } else {
                        if (getId(ex).isPresent()) {
                            handleGetEpicByIdResponse(ex, getId(ex).get());
                        } else {
                            sendIncorrectId(ex);
                        }
                    }
                    break;
                case "DELETE":
                    if (getId(ex).isPresent()) {
                        handleDeleteEpic(ex, getId(ex).get());
                    } else {
                        sendIncorrectId(ex);
                    }
                    break;
                case "POST":
                    handlePostEpic(ex);
                    break;
                default:
                    sendIncorrectMethod(ex);
                    break;
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }

    public void handleGetEpicsResponse(HttpExchange ex) {
        try {
            if (taskManager.getListOfEpics().isEmpty()) {
                sendText(ex, "Список эпиков пуст", 404);
            } else {
                sendText(ex, gson.toJson(taskManager.getListOfEpics()), 200);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }

    public void handleGetEpicByIdResponse(HttpExchange ex, int id) throws IOException {
        if (!taskManager.getListOfEpics().contains(id)) {
            sendText(ex, "Эпик не найден", 404);
        } else {
            sendText(ex, gson.toJson(taskManager.getEpicById(id)), 200);
        }
    }

    public void handleDeleteEpic(HttpExchange ex, int id) throws IOException {
        if (!taskManager.getListOfEpics().contains(id)) {
            sendText(ex, "Эпик не найден", 404);
        } else {
            taskManager.removeEpicById(id);
            sendText(ex, "Эпик с id " + id + " удален", 200);
        }
    }

    public void handlePostEpic(HttpExchange ex) {
        try {
            String exchange = bodyToString(ex);
            if (exchange.isEmpty()) {
                sendText(ex, "Ничего не передано", 400);
            } else {
                Epic epic = gson.fromJson(exchange, Epic.class);
                Optional<Integer> id = Optional.of(epic.getId());
                if (id.get() == 0) {
                    taskManager.addEpic(epic);
                    sendText(ex, "Эпик добавлен", 201);
                } else {
                    taskManager.updateEpic(epic);
                    sendText(ex, "Эпик с id " + id.get() + " обновлен", 201);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }
}
