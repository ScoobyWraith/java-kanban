package api.hadlers;

import api.Settings;
import api.StatusCode;
import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerTaskNotFound;
import manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager manager;

    protected Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;

        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            boolean handled = handleRequest(exchange);

            if (!handled) {
                sendNotFound(exchange);
            }
        } catch (Exception exception) {
            System.out.println("Во время обработки запроса произошло непредвиденное исключение.");
            exception.printStackTrace();
        }
    }

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

    protected void handleGetTaskById(HttpExchange exchange, String path) throws IOException {
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

    protected void handleDeleteTask(HttpExchange exchange, String path) throws IOException {
        Optional<Integer> taskIdOpt = getIntFromSecondPathElement(path);

        if (taskIdOpt.isEmpty()) {
            sendInternalServerError(exchange, "Невозможно обработать ИД в запросе.");
            return;
        }

        deleteTask(taskIdOpt.get());
    }

    protected void sendOnlyStatus(HttpExchange h, int rCode) throws IOException {
        h.sendResponseHeaders(rCode, 0);
        h.close();
    }

    protected void sendStatusAndText(HttpExchange h, int rCode, String text) throws IOException {
        byte[] resp = text.getBytes(Settings.DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", Settings.CONTENT_TYPE);
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendStatusOk(HttpExchange h) throws IOException {
        sendOnlyStatus(h, StatusCode.OK_WITHOUT_BODY.getCode());
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendStatusAndText(h, StatusCode.OK_WITH_BODY.getCode(), text);
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendOnlyStatus(h, StatusCode.NOT_FOUND.getCode());
    }

    protected void sendNotFound(HttpExchange h, String message) throws IOException {
        sendStatusAndText(h, StatusCode.NOT_FOUND.getCode(), String.format("{message: \"%s\"}", message));
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        sendOnlyStatus(h, StatusCode.NOT_ACCEPTABLE.getCode());
    }

    protected void sendInternalServerError(HttpExchange h, String error) throws IOException {
        sendStatusAndText(h, StatusCode.INTERNAL_SERVER_ERROR.getCode(), String.format("{error: \"%s\"}", error));
    }

    protected void sendBadJsonBody(HttpExchange h) throws IOException {
        sendStatusAndText(h, StatusCode.BAD_REQUEST.getCode(), "{error: \"Ошибка тела запроса. Ожидается JSON.\"}");
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
