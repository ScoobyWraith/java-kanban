package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void addNewTasks() {
        final Task createdTask = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Task savedTask = taskManager.getTaskById(createdTask.getId());

        Assertions.assertNotNull(savedTask, "Задача не сохранилась");
        Assertions.assertEquals(createdTask, savedTask, "Созданная и сохраненная задачи не совпадают");

        List<Task> tasks = taskManager.getAllTasks();

        Assertions.assertNotNull(tasks, "Список задач не возвращается");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач в списке");
        Assertions.assertEquals(savedTask, tasks.get(0), "Неверный состав в списке задач");

        final Task repeatedTask = taskManager.createAndAddTask(createdTask);

        Assertions.assertNotEquals(createdTask, repeatedTask, "Задачи имеют одинаковый ИД");
    }

    @Test
    public void addNewSubtasks() {
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask createdSubtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));
        final Subtask savedSubtask = taskManager.getSubtaskById(createdSubtask.getId());

        Assertions.assertNotNull(savedSubtask, "Подзадача не сохранилась");
        Assertions.assertEquals(createdSubtask, savedSubtask, "Созданная и сохраненная подзадачи не совпадают");

        List<Subtask> tasks = taskManager.getAllSubtasks();

        Assertions.assertNotNull(tasks, "Список подзадач не возвращается");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество подззадач в списке");
        Assertions.assertEquals(savedSubtask, tasks.get(0), "Неверный состав в списке подззадач");

        final Subtask repeatedSubtask = taskManager.createAndAddSubtask(createdSubtask);

        Assertions.assertNotEquals(createdSubtask, repeatedSubtask, "Подзадачи имеют одинаковый ИД");

        final Subtask subtaskWithoutEpic
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, -1));

        Assertions.assertNull(subtaskWithoutEpic, "Подзадача создается без эпика");
    }

    @Test
    public void addNewEpics() {
        final Epic createdEpic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Epic savedEpic = taskManager.getEpicById(createdEpic.getId());

        Assertions.assertNotNull(savedEpic, "Эпик не сохранился");
        Assertions.assertEquals(createdEpic, savedEpic, "Созданный и сохраненный эпики не совпадают");

        List<Epic> tasks = taskManager.getAllEpics();

        Assertions.assertNotNull(tasks, "Список эпиков не возвращается");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество эпиков в списке");
        Assertions.assertEquals(savedEpic, tasks.get(0), "Неверный состав в списке эпиков");

        final Epic repeatedEpic = taskManager.createAndAddEpic(createdEpic);

        Assertions.assertNotEquals(createdEpic, repeatedEpic, "Эпики имеют одинаковый ИД");

        final Subtask subtask1 = taskManager
                .createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, createdEpic.getId()));
        final Subtask subtask2 = taskManager.createAndAddSubtask(subtask1);
        List<Subtask> subtasksOfEpic = taskManager.getAllSubtasksInEpic(createdEpic.getId());

        Assertions.assertTrue(subtasksOfEpic.contains(subtask1), "Нет первой подзадачи у эпика");
        Assertions.assertTrue(subtasksOfEpic.contains(subtask2), "Нет второй подзадачи у эпика");

        List<Subtask> subtasksOfRepeatedEpic = taskManager.getAllSubtasksInEpic(repeatedEpic.getId());

        Assertions.assertEquals(subtasksOfRepeatedEpic.size(), 0, "Подзадачи добавились не тому эпику");
    }

    @Test
    public void taskInManagerIsSameAsOriginal() {
        final Task originalTask = new Task("a", "a", TaskStatus.NEW);
        final Task taskFromManager = taskManager.createAndAddTask(originalTask);

        Assertions.assertEquals(
                taskFromManager.getTitle(),
                "a",
                "Название задач не совпадает"
        );
        Assertions.assertEquals(
                taskFromManager.getDescription(),
                "a",
                "Описание задач не совпадает"
        );
        Assertions.assertEquals(
                taskFromManager.getStatus(),
                TaskStatus.NEW,
                "Статус задач не совпадает"
        );
    }

    @Test
    public void updateTasks() {
        final Task originalTask = new Task("a", "a", TaskStatus.NEW);
        final Task taskFromManager = taskManager.createAndAddTask(originalTask);
        taskFromManager.setTitle("b");
        taskFromManager.setDescription("b");
        taskFromManager.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskFromManager);
        final Task updatedTaskFromManager = taskManager.getTaskById(taskFromManager.getId());

        Assertions.assertEquals(
                updatedTaskFromManager.getTitle(),
                "b",
                "Название задач не совпадает после обновления"
        );
        Assertions.assertEquals(
                updatedTaskFromManager.getDescription(),
                "b",
                "Описание задач не совпадает после обновления"
        );
        Assertions.assertEquals(
                updatedTaskFromManager.getStatus(),
                TaskStatus.IN_PROGRESS,
                "Статус задач не совпадает после обновления"
        );
    }

    @Test
    public void removeTasks() {
        final Task task1 = taskManager.createAndAddTask(new Task("a", "a", TaskStatus.NEW));
        taskManager.createAndAddTask(new Task("b", "b", TaskStatus.NEW));
        taskManager.removeTaskById(task1.getId());

        Assertions.assertNull(taskManager.getTaskById(task1.getId()), "Задача не была удалена");

        taskManager.removeAllTasks();

        Assertions.assertEquals(taskManager.getAllTasks().size(), 0, "Список задач не пуст");
    }

    @Test
    public void addTasksInHistoryOverGettingFromManager() {
        final Task task = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask subtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(3, history.size(), "Неверный размер истории в менеджере задач");
    }

    @Test
    public void removeTasksFromHistoryOverTaskManager() {
        final Task task = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask subtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.removeEpicById(epic.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(
                1,
                history.size(),
                "Неверный размер истории в менеджере задач после удаления эпика и связанной подзадачи"
        );
    }
}
