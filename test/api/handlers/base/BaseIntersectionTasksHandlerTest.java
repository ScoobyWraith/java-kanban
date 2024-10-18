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
                "�������� ��� ����������� ������ � ������������ �������"
        );

        Assertions.assertEquals(1, getSizeOfTaskListInManager(), "������������ ���������� �����");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        int id = createTaskAndAddToManager("Task 1", "Desc 1");
        String taskJson = getJsonByIdFromManager(id);
        JsonElement jsonElement = JsonParser.parseString(taskJson);
        jsonElement.getAsJsonObject().addProperty("title", "Updated title");
        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = post(client, url, jsonElement.toString());

        Assertions.assertEquals(201, response.statusCode(), "�������� ��� ������� ����������� ������");
        Assertions.assertEquals(1, getSizeOfTaskListInManager(), "������������ ���������� �����");
        Assertions.assertEquals(
                "Updated title",
                getTitleByPosition(0),
                "�������� ������ �� ����������"
        );
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
                "�������� ��� ����������� ������ � ������������ �������"
        );
        Assertions.assertEquals(2, getSizeOfTaskListInManager(), "������������ ���������� �����");
        Assertions.assertEquals(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                getStartTimeById(id),
                "����� ������ ������ ����������"
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

    protected abstract String getJsonByIdFromManager(int id);

    protected abstract LocalDateTime getStartTimeById(int id);
}
