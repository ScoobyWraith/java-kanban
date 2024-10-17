package api.hadlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerTaskNotFound;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class EpicHandler extends BaseTasksHandler {
    public static String handlePath = "/epics";

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected boolean handleRequest(HttpExchange exchange) throws IOException {
        boolean handled = super.handleRequest(exchange);

        if (!handled) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String regExp = "^" + handlePath + "/\\d+/subtasks$";

            if (method.equals("GET") && Pattern.matches(regExp, path)) {
                showSubtasks(exchange, path);
                return true;
            }
        }

        return false;
    }

    protected void showSubtasks(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> epicIdOpt = getIntFromSecondPathElement(path);

        if (epicIdOpt.isEmpty()) {
            sendInternalServerError(exchange, "Невозможно обработать ИД в запросе.");
            return;
        }

        try {
            List<Subtask> subtasks = manager.getEpicSubtasks(epicIdOpt.get());
            sendText(exchange, gson.toJson(subtasks));
        } catch (ManagerTaskNotFound exception) {
            sendNotFound(exchange, exception.getMessage());
        }
    }

    @Override
    protected String getHandlePath() {
        return handlePath;
    }

    @Override
    protected void showById(HttpExchange exchange, int id) throws ManagerTaskNotFound, IOException {
        Epic task = manager.getEpicById(id);
        sendText(exchange, gson.toJson(task));
    }

    @Override
    protected void showTasks(HttpExchange exchange) throws IOException {
        List<Epic> tasks = manager.getEpics();
        sendText(exchange, gson.toJson(tasks));
    }

    @Override
    protected void deleteTask(int id) {
        manager.deleteEpic(id);
    }

    @Override
    protected void handlePost(HttpExchange exchange, String postBody) throws IOException {
        Epic task = gson.fromJson(postBody, Epic.class);
        manager.createEpic(task);
        sendStatusOk(exchange);
    }
}
