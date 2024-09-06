package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;
import exceptions.ManagerSaveException;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
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
                        handleGetHistory(ex);
                    }
                    break;
                default:
                    sendIncorrectMethod(ex);
                    break;
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }

    public void handleGetHistory(HttpExchange ex)  {
        try {
            if (!taskManager.getHistory().isEmpty()) {
                sendText(ex, gson.toJson(taskManager.getHistory()), 200);
            } else {
                sendText(ex, gson.toJson("Список просмотра пуст"), 400);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Во время выполнения запроса произошла ошибка. Проверьте URL" + e.getMessage());
        }
    }
}
