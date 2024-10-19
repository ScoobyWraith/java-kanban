package api.handlers.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseIntersectionTasksHandlerTest extends BaseTasksHandlerTest {
    public BaseIntersectionTasksHandlerTest() throws IOException {
        super();
    }

    @Test
    public void testAddTaskWithIntersection() throws IOException, InterruptedException {
        createTaskWithTimesAndAddToManager("Task 1", "Desc 1",
                Duration.ofMinutes(60), LocalDateTime.now());
        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        String taskJson = getJsonWithTimes("Task 2", "Desc 2",
                Duration.ofMinutes(15), LocalDateTime.now());
        HttpResponse<String> response = post(client, url, taskJson);

        Assertions.assertEquals(
                406,
                response.statusCode(),
                "Неверный код добавленной задачи с пересечением времени"
        );

        Assertions.assertEquals(1, getSizeOfTaskListInManager(), "Некорректное количество задач");
    }

    @Test
    public void testUpdateTaskWithIntersection() throws IOException, InterruptedException {
        int id = createTaskWithTimesAndAddToManager("Task 1", "Desc 1",
                Duration.ofMinutes(60), LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        createTaskWithTimesAndAddToManager("Task 1", "Desc 1",
                Duration.ofMinutes(60), LocalDateTime.of(2024, 1, 1, 0, 0, 0));

        String taskJson = getJsonByIdFromManager(id);
        JsonElement jsonElement = JsonParser.parseString(taskJson);
        jsonElement.getAsJsonObject().addProperty("startTime", "01.01.2024 00:00:00");
        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = post(client, url, jsonElement.toString());

        Assertions.assertEquals(
                406,
                response.statusCode(),
                "Неверный код обновленной задачи с пересечением времени"
        );
        Assertions.assertEquals(2, getSizeOfTaskListInManager(), "Некорректное количество задач");
        Assertions.assertEquals(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                getStartTimeById(id),
                "Время начала задачи обновилось"
        );
    }

    protected abstract int createTaskWithTimesAndAddToManager(String title,
                                                              String description,
                                                              Duration duration,
                                                              LocalDateTime startTime);

    protected abstract String getJsonWithTimes(String title,
                                               String description,
                                               Duration duration,
                                               LocalDateTime startTime);

    protected abstract LocalDateTime getStartTimeById(int id);
}
