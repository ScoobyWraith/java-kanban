package api.hadlers;

import api.Settings;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerTaskNotFound;
import manager.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class BaseTasksHandler extends BaseHttpHandler {
    public BaseTasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected boolean handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String handlePath = getHandlePath();

        String regExpWithId = "^" + handlePath + "/\\d+$";
        String regExpWithoutId = "^" + handlePath + "$";

        switch (method) {
            case "GET": {
                if (Pattern.matches(regExpWithId, path)) {
                    handleGetTaskById(exchange, path);
                    return true;
                }

                if (Pattern.matches(regExpWithoutId, path)) {
                    showTasks(exchange);
                    return true;
                }

                break;
            }

            case "POST": {
                if (!Pattern.matches(regExpWithoutId, path)) {
                    sendNotFound(exchange);
                    return true;
                }

                String taskBody = new String(exchange.getRequestBody().readAllBytes(), Settings.DEFAULT_CHARSET);
                JsonElement jsonElement = JsonParser.parseString(taskBody);

                if (!jsonElement.isJsonObject()) {
                    sendBadJsonBody(exchange);
                    return true;
                }

                handlePost(exchange, taskBody);
                return true;
            }

            case "DELETE": {
                if (Pattern.matches(regExpWithId, path)) {
                    handleDeleteTask(exchange, path);
                    return true;
                }

                break;
            }
        }

        return false;
    }

    private void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> taskIdOpt = getIntFromSecondPathElement(path);

        if (taskIdOpt.isEmpty()) {
            sendInternalServerError(exchange, "Невозможно обработать ИД в запросе.");
            return;
        }

        try {
            showById(exchange, taskIdOpt.get());
        } catch (ManagerTaskNotFound exception) {
            sendNotFound(exchange, exception.getMessage());
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> taskIdOpt = getIntFromSecondPathElement(path);

        if (taskIdOpt.isEmpty()) {
            sendInternalServerError(exchange, "Невозможно обработать ИД в запросе.");
            return;
        }

        deleteTask(taskIdOpt.get());
    }

    protected Optional<Integer> getIntFromSecondPathElement(String path) {
        try {
            return Optional.of(Integer.parseInt(path.split("/")[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    protected abstract String getHandlePath();

    protected abstract void showById(HttpExchange exchange, int id) throws ManagerTaskNotFound, IOException;

    protected abstract void showTasks(HttpExchange exchange) throws IOException;

    protected abstract void deleteTask(int id);

    protected abstract void handlePost(HttpExchange exchange, String postBody) throws IOException;
}
