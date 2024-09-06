package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import exceptions.ManagerSaveException;
import model.Task;
import exceptions.CollisionTaskException;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
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
                        handleGetTasksResponse(ex);
                    } else {
                        if (getId(ex).isPresent()) {
                            handleGetTaskByIdResponse(ex, getId(ex).get());
                        } else {
                            sendIncorrectId(ex);
                        }
                    }
                    break;
                case "DELETE":
                    if (getId(ex).isPresent()) {
                        handleDeleteTask(ex, getId(ex).get());
                    } else {
                        sendIncorrectId(ex);
                    }
                    break;
                case "POST":
                    handlePostTask(ex);
                    break;
                default:
                    sendIncorrectMethod(ex);
                    break;
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }

    public void handleGetTasksResponse(HttpExchange ex) {
        try {
            if (taskManager.getListOfTasks().isEmpty()) {
                sendText(ex, "Список задач пуст", 404);
            } else {
                sendText(ex, gson.toJson(taskManager.getListOfTasks()), 200);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }


    public void handleGetTaskByIdResponse(HttpExchange ex, int id) throws IOException {
        if (!taskManager.getListOfTasks().contains(id)) {
            sendText(ex, "Задача не найдена", 404);
        } else {
            sendText(ex, gson.toJson(taskManager.getTaskById(id)), 200);
        }
    }

    public void handleDeleteTask(HttpExchange ex, int id) throws IOException {
        if (!taskManager.getListOfTasks().contains(id)) {
            sendText(ex, "Задача не найдена", 404);
        } else {
            taskManager.removeTaskById(id);
            sendText(ex, "Задача с id " + id + " удалена", 200);
        }
    }

    public void handlePostTask(HttpExchange ex) throws IOException {
        try {
            String exchange = bodyToString(ex);
            if (exchange.isEmpty()) {
                sendText(ex, "Ничего не передано", 400);
            } else {
                Task task = gson.fromJson(exchange, Task.class);
                Optional<Integer> id = Optional.of(task.getId());
                taskManager.validate(task);

                if (id.get() == 0) {
                    taskManager.addTask(task);
                    sendText(ex, "Задача добавлена", 201);
                } else {
                    taskManager.updateTask(task);
                    sendText(ex, "Задача с id " + id.get() + " обновлена", 201);
                }
            }
        } catch (CollisionTaskException e) {
            sendText(ex, "Задача не добавлена, так как имеет наложение по времени", 406);
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }
}
