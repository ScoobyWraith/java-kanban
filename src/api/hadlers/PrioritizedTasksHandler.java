package api.hadlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    public static final String handlePath = "/prioritized";

    public PrioritizedTasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected boolean handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String regExpWithoutId = "^" + handlePath + "$";

        if (method.equals("GET") && Pattern.matches(regExpWithoutId, path)) {
            Set<Task> tasks = manager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(tasks));

            return true;
        }

        return false;
    }
}