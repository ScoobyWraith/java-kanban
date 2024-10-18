package api.handlers;

import api.hadlers.SubtaskHandler;
import api.handlers.base.BaseIntersectionTasksHandlerTest;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandlerTest extends BaseIntersectionTasksHandlerTest {
    private int epicId = 0;

    public SubtaskHandlerTest() throws IOException {
        super();
    }

    private int getEpicId() {
        if (epicId == 0) {
            epicId = manager.createEpic(new Epic("", "")).getId();
        }

        return epicId;
    }

    @Override
    protected String getEndpoint() {
        return SubtaskHandler.handlePath;
    }

    @Override
    protected String getJsonTask(String title, String description) {
        return gson.toJson(new Subtask(title, description, TaskStatus.NEW, getEpicId()));
    }

    @Override
    protected String getTitleByPosition(int position) {
        return manager.getSubtasks().get(position).getTitle();
    }

    @Override
    protected int createTaskAndAddToManager(String title, String description) {
        Subtask task = manager.createSubtask(new Subtask(title, description, TaskStatus.NEW, getEpicId()));
        return task.getId();
    }

    @Override
    protected int getSizeOfTaskListInManager() {
        return manager.getSubtasks().size();
    }

    @Override
    protected int createTaskWithTimesAndAddToManager(String title,
                                                     String description,
                                                     Duration duration,
                                                     LocalDateTime startTime) {
        Subtask task = manager
                .createSubtask(new Subtask(title, description, TaskStatus.NEW, getEpicId(), duration, startTime));
        return task.getId();
    }

    @Override
    protected String getJsonWithTimes(String title, String description, Duration duration, LocalDateTime startTime) {
        return gson.toJson(new Subtask(title, description, TaskStatus.NEW, getEpicId(), duration, startTime));
    }

    @Override
    protected String getJsonByIdFromManager(int id) {
        return gson.toJson(manager.getSubtaskById(id));
    }

    @Override
    protected LocalDateTime getStartTimeById(int id) {
        return manager.getSubtaskById(id).getStartTime().get();
    }
}
