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

        Assertions.assertEquals(201, response.statusCode(), "Неверный код успешно добавленной задачи");
        Assertions.assertEquals(1, getSizeOfTaskListInManager(), "Некорректное количество задач");

        JsonElement jsonElement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonElement.isJsonObject(), "Вернулся не JSON объект");

        int id = jsonElement.getAsJsonObject().get("id").getAsInt();

        Assertions.assertEquals(
                "Task 1: very important",
                getTitleById(id),
                "Некорректное имя задачи"
        );
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        int id = createTaskAndAddToManager("Task 1", "Desc 1");
        String taskJson = getJsonByIdFromManager(id);
        JsonElement jsonElement = JsonParser.parseString(taskJson);
        jsonElement.getAsJsonObject().addProperty("title", "Updated title");
        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = post(client, url, jsonElement.toString());

        Assertions.assertEquals(201, response.statusCode(), "Неверный код успешно обновленной задачи");
        Assertions.assertEquals(1, getSizeOfTaskListInManager(), "Некорректное количество задач");
        Assertions.assertEquals(
                "Updated title",
                getTitleById(id),
                "Название задачи не обновилось"
        );
    }

    @Test
    public void testGettingTasks() throws IOException, InterruptedException {
        createTaskAndAddToManager("Task 1", "Desc 1");
        createTaskAndAddToManager("Task 2", "Desc 2");
        createTaskAndAddToManager("Task 3", "Desc 3");

        URI url = URI.create(getPathForEndpoint(getEndpoint()));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "Неверный код получения списка задач");

        JsonElement jsonelement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonelement.isJsonArray(), "Вернулся не массив");
        Assertions.assertEquals(
                3,
                jsonelement.getAsJsonArray().size(),
                "Неверное число элементов в массиве"
        );
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        int id = createTaskAndAddToManager("New important task", "Desc");
        URI url = URI.create(getPathForEndpoint(getEndpoint() + "/" + id));
        HttpResponse<String> response = get(client, url);

        Assertions.assertEquals(200, response.statusCode(), "Неверный код получения задачи");

        JsonElement jsonelement = JsonParser.parseString(response.body());

        Assertions.assertTrue(jsonelement.isJsonObject(), "Вернулся не JSON объект");

        Assertions.assertEquals(
                "New important task",
                jsonelement.getAsJsonObject().get("title").getAsString(),
                "Получена неверная задача"
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
                "Неверный код получения несуществующей задачи"
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
                "Неверный код удаления задачи"
        );

        Assertions.assertEquals(
                0,
                getSizeOfTaskListInManager(),
                "Задача не была удалена"
        );
    }

    protected abstract String getJsonTask(String title, String description);

    protected abstract String getTitleById(int id);

    protected abstract int createTaskAndAddToManager(String title, String description);

    protected abstract int getSizeOfTaskListInManager();

    protected abstract String getJsonByIdFromManager(int id);
}
