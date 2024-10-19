package api.handlers;

import api.hadlers.TaskHandler;
import api.handlers.base.BaseIntersectionTasksHandlerTest;
import model.Task;
import model.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskHandlerTest extends BaseIntersectionTasksHandlerTest {
    public TaskHandlerTest() throws IOException {
        super();
    }

    @Override
    protected String getEndpoint() {
        return TaskHandler.handlePath;
    }

    @Override
    protected String getJsonTask(String title, String description) {
        return gson.toJson(new Task(title, description, TaskStatus.NEW));
    }

    @Override
    protected String getTitleById(int id) {
        return manager.getTaskById(id).getTitle();
    }

    @Override
    protected int createTaskAndAddToManager(String title, String description) {
        Task task = manager.createTask(new Task(title, description, TaskStatus.NEW));
        return task.getId();
    }

    @Override
    protected int getSizeOfTaskListInManager() {
        return manager.getTasks().size();
    }

    @Override
    protected int createTaskWithTimesAndAddToManager(String title,
                                                     String description,
                                                     Duration duration,
                                                     LocalDateTime startTime) {
        Task task = manager.createTask(new Task(title, description, TaskStatus.NEW, duration, startTime));
        return task.getId();
    }

    @Override
    protected String getJsonWithTimes(String title, String description, Duration duration, LocalDateTime startTime) {
        return gson.toJson(new Task(title, description, TaskStatus.NEW, duration, startTime));
    }

    @Override
    protected String getJsonByIdFromManager(int id) {
        return gson.toJson(manager.getTaskById(id));
    }

    @Override
    protected LocalDateTime getStartTimeById(int id) {
        return manager.getTaskById(id).getStartTime().get();
    }
}
