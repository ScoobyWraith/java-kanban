package api.hadlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerTaskNotFound;
import exceptions.ManagerTaskTimeIntersection;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseTasksHandler {
    public static final String handlePath = "/tasks";

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected String getHandlePath() {
        return handlePath;
    }

    @Override
    protected void showById(HttpExchange exchange, int id) throws ManagerTaskNotFound, IOException {
        Task task = manager.getTaskById(id);
        sendText(exchange, gson.toJson(task));
    }

    @Override
    protected void showTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = manager.getTasks();
        sendText(exchange, gson.toJson(tasks));
    }

    @Override
    protected void deleteTask(int id) {
        manager.deleteTask(id);
    }

    @Override
    protected void handlePost(HttpExchange exchange, String postBody) throws IOException {
        Task task = null;

        try {
            task = gson.fromJson(postBody, Task.class);
        } catch (Exception exception) {
            sendInternalServerError(exchange, "Невозможно преобразовать JSON.");
            return;
        }

        try {
            if (task.getId() > 0) {
                manager.updateTask(task);
            } else {
                manager.createTask(task);
            }

            sendStatusOk(exchange);
        } catch (ManagerTaskTimeIntersection exception) {
            sendHasInteractions(exchange);
        }
    }
}
