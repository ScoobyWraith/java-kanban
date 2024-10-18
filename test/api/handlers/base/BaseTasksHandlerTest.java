package api.handlers.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

public abstract class BaseTasksHandlerTest extends CommonHandlerTest {
    public BaseTasksHandlerTest() throws IOException {
        super();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = getJsonTask("Task 1: very important", "Desc 1");
        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = post(client, url, taskJson);

        Assertions.assertEquals(201, response.statusCode(), "�������� ��� ������� ����������� ������");

        Assertions.assertEquals(1, getSizeOfTaskListInManager(), "������������ ���������� �����");
        Assertions.assertEquals(
                "Task 1: very important",
                getTitleByPosition(0),
                "������������ ��� ������"
        );
    }

    @Test
    public void testGettingTasks() throws IOException, InterruptedException {
        createTaskAndAddToManager("Task 1", "Desc 1");
        createTaskAndAddToManager("Task 2", "Desc 2");
        createTaskAndAddToManager("Task 3", "Desc 3");

        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "�������� ��� ��������� ������ �����");

        JsonElement jsonelement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonelement.isJsonArray(), "�������� �� ������");
        Assertions.assertEquals(
                3,
                jsonelement.getAsJsonArray().size(),
                "�������� ����� ��������� � �������"
        );
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        int id = createTaskAndAddToManager("New important task", "Desc");
        URI url = URI.create(getPathForEndpoint(getEndpoint() + "/" + id));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "�������� ��� ��������� ������");

        JsonElement jsonelement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonelement.isJsonObject(), "�������� �� JSON ������");

        Assertions.assertEquals(
                "New important task",
                jsonelement.getAsJsonObject().get("title").getAsString(),
                "�������� �������� ������"
        );
    }

    @Test
    public void testGetUndefinedTaskById() throws IOException, InterruptedException {
        int id = 100;
        URI url = URI.create(getPathForEndpoint(getEndpoint() + "/" + id));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(
                404,
                response.statusCode(),
                "�������� ��� ��������� �������������� ������"
        );
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        int id = createTaskAndAddToManager("Task for removing", "Desc");
        URI url = URI.create(getPathForEndpoint(getEndpoint() + "/" + id));
        HttpResponse<String> response = delete(client, url);

        Assertions.assertEquals(
                200,
                response.statusCode(),
                "�������� ��� �������� ������"
        );

        Assertions.assertEquals(
                0,
                getSizeOfTaskListInManager(),
                "������ �� ���� �������"
        );
    }

    protected abstract String getJsonTask(String title, String description);

    protected abstract String getTitleByPosition(int position);

    protected abstract int createTaskAndAddToManager(String title, String description);

    protected abstract int getSizeOfTaskListInManager();
}
