package manager;

import exceptions.ManagerTaskTimeIntersection;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager = createTaskManager();
    }

    @Test
    public void testGetAllTasks() {
        taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        List<Task> all = taskManager.getAllTasks();

        Assertions.assertEquals(2, all.size(), "Вернулся неполный список всех задач");
    }

    @Test
    public void testGetAllSubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        List<Subtask> all = taskManager.getAllSubtasks();

        Assertions.assertEquals(2, all.size(), "Вернулся неполный список всех подзадач");
    }

    @Test
    public void getAllEpics() {
        taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.createAndAddEpic(new Epic("t", "d"));
        List<Epic> all = taskManager.getAllEpics();

        Assertions.assertEquals(2, all.size(), "Вернулся неполный список всех эпиков");
    }

    @Test
    public void testRemoveAllTasks() {
        taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        taskManager.removeAllTasks();
        List<Task> all = taskManager.getAllTasks();

        Assertions.assertEquals(0, all.size(), "Вернулся не пустой список всех задач");
    }

    @Test
    public void testRemoveAllSubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        taskManager.removeAllSubtasks();
        List<Subtask> all = taskManager.getAllSubtasks();

        Assertions.assertEquals(0, all.size(), "Вернулся не пустой список всех подзадач");
    }

    @Test
    public void testRemoveAllEpics() {
        taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.removeAllEpics();
        List<Epic> all = taskManager.getAllEpics();

        Assertions.assertEquals(0, all.size(), "Вернулся не пустой список всех эпиков");
    }

    @Test
    public void testGetTaskById() {
        Task t = taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        Task fromManager = taskManager.getTaskById(t.getId());

        Assertions.assertEquals(t, fromManager, "Менеджер возвращает неверную задачу по ИД");
    }

    @Test
    public void testGetSubtaskById() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask t = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        Subtask fromManager = taskManager.getSubtaskById(t.getId());

        Assertions.assertEquals(t, fromManager, "Менеджер возвращает неверную подзадачу по ИД");
    }

    @Test
    public void testGetEpicById() {
        Epic t = taskManager.createAndAddEpic(new Epic("t", "d"));
        Epic fromManager = taskManager.getEpicById(t.getId());

        Assertions.assertEquals(t, fromManager, "Менеджер возвращает неверный эпик по ИД");
    }

    @Test
    public void testCreateAndAddTask() {
        Task task = taskManager.createAndAddTask(new Task(
                "t",
                "d",
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));

        Assertions.assertNotNull(task, "Задача не создается");
    }

    @Test
    public void testCreateAndAddSubtask() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask task = taskManager.createAndAddSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.NEW,
                epic.getId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));

        Assertions.assertNotNull(task, "Подзадача не создается");
    }

    @Test
    public void testCreateAndAddEpic() {
        Epic task = taskManager.createAndAddEpic(new Epic("t", "d"));
        Assertions.assertNotNull(task, "Эпик не создается");
    }

    @Test
    public void testUpdateTask() {
        Task task = taskManager.createAndAddTask(new Task(
                "t",
                "d",
                TaskStatus.NEW,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));
        Task newVersion = new Task(
                "t2",
                "d2",
                TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(20),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
        newVersion.setId(task.getId());
        taskManager.updateTask(newVersion);
        Task updated = taskManager.getTaskById(task.getId());

        Assertions.assertEquals(newVersion.getTitle(), updated.getTitle(), "Название не обновилось");
        Assertions.assertEquals(newVersion.getDescription(), updated.getDescription(), "Описание не обновилось");
        Assertions.assertEquals(newVersion.getStatus(), updated.getStatus(), "Статус не обновился");
        Assertions.assertEquals(
                newVersion.getDuration().get(),
                updated.getDuration().get(),
                "Продолжительность не обновилась"
        );
        Assertions.assertEquals(
                newVersion.getStartTime().get(),
                updated.getStartTime().get(),
                "Начало не обновилось"
        );
    }

    @Test
    public void updateSubtask() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask task = taskManager.createAndAddSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.NEW,
                epic.getId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));
        Subtask newVersion = new Subtask(
                "t2",
                "d2",
                TaskStatus.IN_PROGRESS,
                epic.getId(),
                Duration.ofMinutes(20),
                LocalDateTime.of(2023, 1, 1, 0, 0)
        );
        newVersion.setId(task.getId());
        taskManager.updateSubtask(newVersion);
        Subtask updated = taskManager.getSubtaskById(task.getId());

        Assertions.assertEquals(newVersion.getTitle(), updated.getTitle(), "Название не обновилось");
        Assertions.assertEquals(newVersion.getDescription(), updated.getDescription(), "Описание не обновилось");
        Assertions.assertEquals(newVersion.getStatus(), updated.getStatus(), "Статус не обновился");
        Assertions.assertEquals(newVersion.getEpicId(), updated.getEpicId(), "Эпик не обновился");
        Assertions.assertEquals(
                newVersion.getDuration().get(),
                updated.getDuration().get(),
                "Продолжительность не обновилась"
        );
        Assertions.assertEquals(
                newVersion.getStartTime().get(),
                updated.getStartTime().get(),
                "Начало не обновилось"
        );
    }

    @Test
    public void testUpdateEpic() {
        Epic task = taskManager.createAndAddEpic(new Epic("t", "d"));
        task.setDuration(Duration.ofMinutes(10));
        task.setStartTime(LocalDateTime.of(2020, 1, 1, 0, 0));

        Epic newVersion = new Epic("t2", "d2");
        newVersion.setId(task.getId());
        newVersion.setStatus(TaskStatus.IN_PROGRESS);
        newVersion.setDuration(Duration.ofMinutes(20));
        newVersion.setStartTime(LocalDateTime.of(2024, 1, 1, 0, 0));

        taskManager.updateEpic(newVersion);
        Epic updated = taskManager.getEpicById(task.getId());

        Assertions.assertEquals(newVersion.getTitle(), updated.getTitle(), "Название не обновилось");
        Assertions.assertEquals(newVersion.getDescription(), updated.getDescription(), "Описание не обновилось");
        Assertions.assertNotEquals(newVersion.getStatus(), updated.getStatus(), "Статус обновился, но не должен был");
        Assertions.assertNotEquals(
                newVersion.getDuration().get(),
                updated.getDuration().get(),
                "Продолжительность обновилась, но не должна была"
        );
        Assertions.assertNotEquals(
                newVersion.getStartTime().get(),
                updated.getStartTime().get(),
                "Начало обновилось, но не должно было"
        );
    }

    @Test
    public void testRemoveTaskById() {
        Task t = taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        taskManager.removeTaskById(t.getId());
        List<Task> all = taskManager.getAllTasks();

        Assertions.assertFalse(all.contains(t), "Задача не была удалена по ИД");
    }

    @Test
    public void testRemoveSubtaskById() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask t = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        taskManager.removeSubtaskById(t.getId());
        List<Subtask> all = taskManager.getAllSubtasks();

        Assertions.assertFalse(all.contains(t), "Подзадача не была удалена по ИД");
    }

    @Test
    public void testRemoveEpicById() {
        Epic t = taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.removeEpicById(t.getId());
        List<Epic> all = taskManager.getAllEpics();

        Assertions.assertFalse(all.contains(t), "Эпик не был удален по ИД");
    }

    @Test
    public void testGetAllSubtasksInEpic() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        List<Subtask> subtasks = taskManager.getAllSubtasksInEpic(epic.getId());

        Assertions.assertEquals(2, subtasks.size(), "Вернулись не все подзадачи для эпика");
    }

    @Test
    public void testGetHistory() {
        Task t = taskManager.createAndAddTask(new Task("t", "d", TaskStatus.NEW));
        Epic e = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, e.getId()));

        taskManager.getSubtaskById(s.getId());
        taskManager.getEpicById(e.getId());
        taskManager.getTaskById(t.getId());

        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(s, history.get(0), "Нарушен порядок в истории для 1го элемента");
        Assertions.assertEquals(e, history.get(1), "Нарушен порядок в истории для 2го элемента");
        Assertions.assertEquals(t, history.get(2), "Нарушен порядок в истории для 3го элемента");
    }

    @Test
    public void testGetPrioritizedTasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s1 = taskManager.createAndAddSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.NEW,
                epic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));
        Task t = taskManager.createAndAddTask(new Task(
                "t",
                "d",
                TaskStatus.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2015, 1, 1, 0, 0)
        ));
        Subtask s2 = taskManager.createAndAddSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.NEW,
                epic.getId(),
                Duration.ofMinutes(15),
                LocalDateTime.of(2019, 1, 1, 0, 0)
        ));

        Object[] prioritizedTasks = taskManager.getPrioritizedTasks().toArray();

        Assertions.assertEquals(t, prioritizedTasks[0], "Нарушен порядок очереди приоритезации для 1го элемента");
        Assertions.assertEquals(s2, prioritizedTasks[1], "Нарушен порядок очереди приоритезации для 2го элемента");
        Assertions.assertEquals(s1, prioritizedTasks[2], "Нарушен порядок очереди приоритезации для 3го элемента");
    }

    @Test
    public void calcEpicTimesBySubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s1 = taskManager.createAndAddSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.NEW,
                epic.getId(),
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 1, 1, 0, 0)
        ));
        Subtask s2 = taskManager.createAndAddSubtask(new Subtask(
                "t",
                "d",
                TaskStatus.NEW,
                epic.getId(),
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 1, 1, 1, 30)
        ));

        LocalDateTime startTime = epic.getStartTime().get();
        LocalDateTime endTime = epic.getEndTime().get();
        Duration duration = epic.getDuration().get();

        Assertions.assertEquals(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                startTime,
                "Время начала эпика расчитано неверно"
        );

        Assertions.assertEquals(
                LocalDateTime.of(2024, 1, 1, 2, 0),
                endTime,
                "Время окончания эпика расчитано неверно"
        );

        Assertions.assertEquals(
                Duration.ofMinutes(40),
                duration,
                "Продолжительность эпика расчитана неверно"
        );
    }

    @Test
    public void calcEpicStatusOnlyNewSubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s1 = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        Subtask s2 = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus(), "Для всех NEW подзадач эпик не NEW");
    }

    @Test
    public void calcEpicStatusOnlyDoneSubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s1 = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.DONE, epic.getId()));
        Subtask s2 = taskManager.createAndAddSubtask(new Subtask("t", "d", TaskStatus.DONE, epic.getId()));
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus(), "Для всех DONE подзадач эпик не DONE");
    }

    @Test
    public void calcEpicStatusOnlyInProgressSubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s1 = taskManager
                .createAndAddSubtask(new Subtask("t", "d", TaskStatus.IN_PROGRESS, epic.getId()));
        Subtask s2 = taskManager
                .createAndAddSubtask(new Subtask("t", "d", TaskStatus.IN_PROGRESS, epic.getId()));
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(
                TaskStatus.IN_PROGRESS,
                epic.getStatus(),
                "Для всех IN_PROGRESS подзадач эпик не IN_PROGRESS"
        );
    }

    @Test
    public void calcEpicStatusWithNewAndDoneSubtasks() {
        Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
        Subtask s1 = taskManager
                .createAndAddSubtask(new Subtask("t", "d", TaskStatus.NEW, epic.getId()));
        Subtask s2 = taskManager
                .createAndAddSubtask(new Subtask("t", "d", TaskStatus.DONE, epic.getId()));
        epic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(
                TaskStatus.IN_PROGRESS,
                epic.getStatus(),
                "Для всех NEW и DONE подзадач эпик не IN_PROGRESS"
        );
    }

    @Test
    public void testThrowExceptionWhileTimeIntersect() {
        Assertions.assertThrows(
                ManagerTaskTimeIntersection.class,
                () -> {
                    Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
                    taskManager.createAndAddTask(new Task(
                            "",
                            "",
                            TaskStatus.NEW,
                            Duration.ofMinutes(60),
                            LocalDateTime.of(2024, 1, 1, 0, 0)
                    ));
                    taskManager.createAndAddSubtask(new Subtask(
                            "",
                            "",
                            TaskStatus.NEW,
                            epic.getId(),
                            Duration.ofMinutes(60),
                            LocalDateTime.of(2024, 1, 1, 0, 25)
                    ));
                },
                "Попытка добавить задачи с пересекающимися временными интервалами не привела к выбросу исключения"
        );
    }

    @Test
    public void testDontThrowExceptionWhileTimeIntersect() {
        Assertions.assertDoesNotThrow(
                () -> {
                    Epic epic = taskManager.createAndAddEpic(new Epic("t", "d"));
                    taskManager.createAndAddTask(new Task(
                            "",
                            "",
                            TaskStatus.NEW,
                            Duration.ofMinutes(60),
                            LocalDateTime.of(2024, 1, 1, 0, 0)
                    ));
                    taskManager.createAndAddSubtask(new Subtask(
                            "",
                            "",
                            TaskStatus.NEW,
                            epic.getId(),
                            Duration.ofMinutes(30),
                            LocalDateTime.of(2024, 1, 1, 1, 0)
                    ));
                },
                "Попытка добавить задачи с последовательными временными интервалами приела к выбросу исключения"
        );
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
