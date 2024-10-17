package api.hadlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {
    public static final String handlePath = "/history";

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected boolean handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String regExpWithoutId = "^" + handlePath + "$";

        if (method.equals("GET") && Pattern.matches(regExpWithoutId, path)) {
            List<Task> tasks = manager.getHistory();
            sendText(exchange, gson.toJson(tasks));

            return true;
        }

        return false;
    }
}
