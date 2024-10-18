package api.handlers;

import api.hadlers.EpicHandler;
import api.handlers.base.BaseTasksHandlerTest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

public class EpicHandlerTest extends BaseTasksHandlerTest {
    public EpicHandlerTest() throws IOException {
        super();
    }

    @Override
    protected String getEndpoint() {
        return EpicHandler.handlePath;
    }

    @Override
    protected String getJsonTask(String title, String description) {
        return gson.toJson(new Epic(title, description));
    }

    @Override
    protected String getTitleByPosition(int position) {
        return manager.getEpics().get(position).getTitle();
    }

    @Override
    protected int createTaskAndAddToManager(String title, String description) {
        Epic task = manager.createEpic(new Epic(title, description));
        return task.getId();
    }

    @Override
    protected int getSizeOfTaskListInManager() {
        return manager.getEpics().size();
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("a", "b"));

        manager.createSubtask(new Subtask("Subtask 1", "description", TaskStatus.NEW, epic.getId()));
        manager.createSubtask(new Subtask("Subtask 2", "description", TaskStatus.NEW, epic.getId()));
        manager.createSubtask(new Subtask("Subtask 3", "description", TaskStatus.NEW, epic.getId()));
        manager.createSubtask(new Subtask("Subtask 4", "description", TaskStatus.NEW, epic.getId()));

        URI url = URI.create(getPathForEndpoint(getEndpoint() + String.format("/%d/subtasks", epic.getId())));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "Неверный код получения списка задач");

        JsonElement jsonelement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonelement.isJsonArray(), "Вернулся не массив");
        Assertions.assertEquals(
                4,
                jsonelement.getAsJsonArray().size(),
                "Неверное число элементов в массиве"
        );
    }

    @Test
    public void testGetUndefinedEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("a", "b"));

        int subtaskId = manager.createSubtask(new Subtask("Subtask 1", "description",
                TaskStatus.NEW, epic.getId())).getId();
        manager.createSubtask(new Subtask("Subtask 2", "description", TaskStatus.NEW, epic.getId()));

        URI url = URI.create(getPathForEndpoint(getEndpoint() + String.format("/%d/subtasks", subtaskId)));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(404, response.statusCode(), "Неверный код отсутсвия эпика");
    }
}
