package api.handlers;

import api.hadlers.HistoryHandler;
import api.handlers.base.CommonHandlerTest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

public class HistoryHandlerTest extends CommonHandlerTest {
    public HistoryHandlerTest() throws IOException {
        super();
    }

    @Override
    protected String getEndpoint() {
        return HistoryHandler.handlePath;
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Epic", "description"));
        Subtask s1 = manager.createSubtask(new Subtask("Subtask 1", "description",
                TaskStatus.NEW, epic.getId()));
        Task t = manager.createTask(new Task("Task 1", "description", TaskStatus.NEW));
        Subtask s2 = manager.createSubtask(new Subtask("Subtask 2", "description",
                TaskStatus.NEW, epic.getId()));

        manager.getTaskById(t.getId());
        manager.getSubtaskById(s2.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(s1.getId());

        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "Неверный код получения списка задач");

        JsonElement jsonElement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonElement.isJsonArray(), "Вернулся не массив");

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        Assertions.assertEquals(
                4,
                jsonArray.size(),
                "Неверное число элементов в массиве"
        );

        Assertions.assertEquals(
                "Task 1",
                jsonArray.get(0).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок в истории для 1го элемента"
        );
        Assertions.assertEquals(
                "Subtask 2",
                jsonArray.get(1).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок в истории для 2го элемента"
        );
        Assertions.assertEquals(
                "Epic",
                jsonArray.get(2).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок в истории для 3го элемента"
        );
        Assertions.assertEquals(
                "Subtask 1",
                jsonArray.get(3).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок в истории для 4го элемента"
        );
    }
}
