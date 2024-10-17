package api.hadlers;

import api.Settings;
import api.StatusCode;
import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager manager;

    protected Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;

        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
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

    protected abstract boolean handleRequest(HttpExchange exchange) throws IOException;
}
