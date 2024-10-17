package api.handlers;

import api.hadlers.TaskHandler;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandlerTest extends CommonHandlerTest {
    private final String endpoint = TaskHandler.handlePath;

    public TaskHandlerTest() throws IOException {
        super();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);
        URI url = URI.create(getPathForEndpoint(endpoint));
        HttpResponse<String> response = post(client, url, taskJson);

        Assertions.assertEquals(201, response.statusCode(), "Неверный код успешно добавленной задачи");

        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testTaskWithIntersection() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());

        URI url = URI.create(getPathForEndpoint(endpoint));
        String taskJson = gson.toJson(task);
        post(client, url, taskJson);

        task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());

        taskJson = gson.toJson(task);
        HttpResponse<String> response = post(client, url, taskJson);

        Assertions.assertEquals(
                406,
                response.statusCode(),
                "Неверный код добавленной задачи с пересечением"
        );

        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);
        URI url = URI.create(getPathForEndpoint(endpoint));
        post(client, url, taskJson);
        Task createdTask = manager.getTasks().get(0);
        createdTask.setStatus(TaskStatus.DONE);
        taskJson = gson.toJson(createdTask);
        HttpResponse<String> response = post(client, url, taskJson);

        Assertions.assertEquals(201, response.statusCode(), "Неверный код успешно обновленной задачи");

        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(TaskStatus.DONE, tasksFromManager.get(0).getStatus(), "Некорректный статус задачи");
    }

    @Test
    public void testUpdateTaskWithIntersection() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());

        URI url = URI.create(getPathForEndpoint(endpoint));
        String taskJson = gson.toJson(task);
        post(client, url, taskJson);

        task = new Task("Test 2", "Testing task 2", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2020, 1, 1, 0, 0));

        taskJson = gson.toJson(task);
        post(client, url, taskJson);
        Task createdTask = manager.getTasks().get(1);
        createdTask.setStartTime(LocalDateTime.now());
        taskJson = gson.toJson(createdTask);
        HttpResponse<String> response = post(client, url, taskJson);

        Assertions.assertEquals(
                406,
                response.statusCode(),
                "Неверный код обновленной задачи с пересечением"
        );

        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }
}
