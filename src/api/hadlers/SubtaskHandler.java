package api.hadlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerTaskNotFound;
import exceptions.ManagerTaskTimeIntersection;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseTasksHandler {
    public static final String handlePath = "/subtasks";

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected String getHandlePath() {
        return handlePath;
    }

    @Override
    protected void showById(HttpExchange exchange, int id) throws ManagerTaskNotFound, IOException {
        Subtask task = manager.getSubtaskById(id);
        sendText(exchange, gson.toJson(task));
    }

    @Override
    protected void showTasks(HttpExchange exchange) throws IOException {
        List<Subtask> tasks = manager.getSubtasks();
        sendText(exchange, gson.toJson(tasks));
    }

    @Override
    protected void deleteTask(int id) {
        manager.deleteSubtask(id);
    }

    @Override
    protected void handlePost(HttpExchange exchange, String postBody) throws IOException {
        Subtask task = null;

        try {
            task = gson.fromJson(postBody, Subtask.class);
        } catch (Exception exception) {
            sendInternalServerError(exchange, "Невозможно преобразовать JSON.");
            return;
        }

        try {
            if (task.getId() > 0) {
                manager.updateSubtask(task);
            } else {
                manager.createSubtask(task);
            }

            sendStatusOk(exchange);
        } catch (ManagerTaskTimeIntersection exception) {
            sendHasInteractions(exchange);
        }
    }
}
