package api.handlers;

import api.hadlers.PrioritizedTasksHandler;
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
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedTasksHandlerTest extends CommonHandlerTest {
    public PrioritizedTasksHandlerTest() throws IOException {
        super();
    }

    @Override
    protected String getEndpoint() {
        return PrioritizedTasksHandler.handlePath;
    }

    @Test
    public void testPrioritizedTasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("epic", "description"));
        manager.createSubtask(new Subtask("Subtask 1", "description", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 0, 0)));
        manager.createTask(new Task("Task 1",
                "description", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2015, 1, 1, 0, 0)));
        manager.createSubtask(new Subtask("Subtask 2",
                "description", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.of(2019, 1, 1, 0, 0)));

        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "Неверный код получения списка задач");

        JsonElement jsonElement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonElement.isJsonArray(), "Вернулся не массив");

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        Assertions.assertEquals(
                3,
                jsonArray.size(),
                "Неверное число элементов в массиве"
        );

        Assertions.assertEquals(
                "Task 1",
                jsonArray.get(0).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок очереди приоритезации для 1го элемента"
        );
        Assertions.assertEquals(
                "Subtask 2",
                jsonArray.get(1).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок очереди приоритезации для 2го элемента"
        );
        Assertions.assertEquals(
                "Subtask 1",
                jsonArray.get(2).getAsJsonObject().get("title").getAsString(),
                "Нарушен порядок очереди приоритезации для 3го элемента"
        );
    }
}
