package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return (FileBackedTaskManager) Managers.getFileBackedManager(tmpFile);
    }

    private static File tmpFile;

    @BeforeAll
    public static void beforeAll() throws IOException {
        tmpFile = File.createTempFile("java-kanban-file-backed-manager-data", ".csv");
    }

    @AfterAll
    public static void afterAll() throws IOException {
        if (tmpFile != null) {
            Files.deleteIfExists(tmpFile.toPath());
        }
    }

    @AfterEach
    public void afterEach() throws IOException {
        if (tmpFile != null) {
            Files.writeString(tmpFile.toPath(), "");
        }
    }

    @Test
    public void managerLoadFromEmptyFile() {
        List<Task> tasks = taskManager.getTasks();
        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtasks();

        Assertions.assertEquals(
                0,
                tasks.size() + epics.size() + subtasks.size(),
                "FileBackedTaskManager имеет задачи"
        );
    }

    @Test
    public void managerSaveEmptyFile() {
        taskManager.save();

        Assertions.assertEquals(
                0,
                tmpFile.length(),
                "Пустой FileBackedTaskManager сохранил не пустой файл"
        );
    }

    @Test
    public void saveAndLoadTasks() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Desc", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Desc", TaskStatus.NEW));

        Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Desc"));

        Subtask subtask1_1 = taskManager
                .createSubtask(new Subtask("Sub 1_1", "Desc", TaskStatus.NEW, epic1.getId()));
        Subtask subtask1_2 = taskManager
                .createSubtask(new Subtask("Sub 1_2", "Desc", TaskStatus.NEW, epic1.getId()));

        Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Desc"));

        Subtask subtask2 = taskManager
                .createSubtask(new Subtask("Sub 2", "Desc", TaskStatus.NEW, epic2.getId()));

        task2.setTitle("New title");

        // Done subtask2 and ==> epic2
        subtask2.setStatus(TaskStatus.DONE);

        subtask1_1.setStatus(TaskStatus.IN_PROGRESS);
        subtask1_2.setDescription("New desc");

        // end saving, start loading

        TaskManager managerToLoad = Managers.getFileBackedManager(tmpFile);
        Task task1FromFile = managerToLoad.getTaskById(task1.getId());
        Assertions.assertEquals(task1, task1FromFile, "Неверные сохранение/загрузка заадчи 1");

        Task task2FromFile = managerToLoad.getTaskById(task2.getId());
        Assertions.assertEquals(task2, task2FromFile, "Неверные сохранение/загрузка заадчи 2");

        Subtask subtask1_1FromFile = managerToLoad.getSubtaskById(subtask1_1.getId());
        Assertions.assertEquals(subtask1_1, subtask1_1FromFile, "Неверные сохранение/загрузка подзадачи");

        Epic epic2FromFile = managerToLoad.getEpicById(epic2.getId());
        Assertions.assertEquals(epic2, epic2FromFile, "Неверные сохранение/загрузка эпика");
    }

    @Test
    public void checkSaveLoadAllSubtaskFields() {
        Epic epic = taskManager.createEpic(new Epic("t", "d"));
        Subtask s = taskManager.createSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.IN_PROGRESS,
                epic.getId(),
                Duration.ofMinutes(50),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));

        TaskManager managerToLoad = Managers.getFileBackedManager(tmpFile);
        Subtask fromFile = managerToLoad.getSubtaskById(s.getId());

        Assertions.assertEquals(s.getId(), fromFile.getId(), "Неверный ИД");
        Assertions.assertEquals(s.getTitle(), fromFile.getTitle(), "Неверное название");
        Assertions.assertEquals(s.getDescription(), fromFile.getDescription(), "Неверное описание");
        Assertions.assertEquals(s.getStatus(), fromFile.getStatus(), "Неверный статус");
        Assertions.assertEquals(s.getEpicId(), fromFile.getEpicId(), "Неверный эпик ИД");
        Assertions.assertEquals(
                s.getDuration().get(),
                fromFile.getDuration().get(),
                "Неверная продолжительность"
        );
        Assertions.assertEquals(
                s.getStartTime().get(),
                fromFile.getStartTime().get(),
                "Неверная дата начала"
        );
    }

    @Test
    public void testThrowExceptionOnSaveFile() {
        Assertions.assertDoesNotThrow(
                () -> {
                    File notExistedFile = File.createTempFile("tmp", ".csv");
                    Files.deleteIfExists(notExistedFile.toPath());
                    taskManager = new FileBackedTaskManager(notExistedFile);
                    Task task = taskManager.createTask(new Task("t", "d", TaskStatus.NEW));

                    Files.deleteIfExists(notExistedFile.toPath());
                },
                "Попытка сохранения в несуществующий файл выбросила исключение"
        );
    }
}