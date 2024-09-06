package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import exceptions.ManagerSaveException;
import model.Subtask;
import exceptions.CollisionTaskException;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
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
                        handleGetSubtasksResponse(ex);
                    } else {
                        if (getId(ex).isPresent()) {
                            handleGetSubtaskByIdResponse(ex, getId(ex).get());
                        } else {
                            sendIncorrectId(ex);
                        }
                    }
                    break;
                case "DELETE":
                    if (getId(ex).isPresent()) {
                        handleDeleteSubtask(ex, getId(ex).get());
                    } else {
                        sendIncorrectId(ex);
                    }
                    break;
                case "POST":
                    handlePostSubtask(ex);
                    break;
                default:
                    sendIncorrectMethod(ex);
                    break;
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }

    public void handleGetSubtasksResponse(HttpExchange ex) {
        try {
            if (taskManager.getListOfSubtasks().isEmpty()) {
                sendText(ex, "Список задач пуст", 404);
            } else {
                sendText(ex, gson.toJson(taskManager.getListOfSubtasks()), 200);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }

    public void handleGetSubtaskByIdResponse(HttpExchange ex, int id) throws IOException {
        if (!taskManager.getListOfSubtasks().contains(id)) {
            sendText(ex, "Задача не найдена", 404);
        } else {
            sendText(ex, gson.toJson(taskManager.getSubtaskById(id)), 200);
        }
    }

    public void handleDeleteSubtask(HttpExchange ex, int id) throws IOException {
        if (!taskManager.getListOfSubtasks().contains(id)) {
            sendText(ex, "Задача не найдена", 404);
        } else {
            taskManager.removeSubtaskById(id);
            sendText(ex, "Подзадача с id " + id + " удалена", 200);
        }
    }

    public void handlePostSubtask(HttpExchange ex) throws IOException {
        try {
            String exchange = bodyToString(ex);
            if (exchange.isEmpty()) {
                sendText(ex, "Ничего не передано", 400);
            } else {
                Subtask subtask = gson.fromJson(exchange, Subtask.class);
                Optional<Integer> id = Optional.of(subtask.getId());
                taskManager.validate(subtask);

                if (id.get() == 0) {
                    taskManager.addSubtask(subtask);
                    sendText(ex, "Подзадача добавлена", 201);
                } else {
                    taskManager.updateSubtask(subtask);
                    sendText(ex, "Подзадача с id " + id.get() + " обновлена", 201);
                }
            }
        } catch (CollisionTaskException e) {
            sendText(ex, "Подзадача не добавлена, так как имеет наложение по времени", 406);
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }
}
