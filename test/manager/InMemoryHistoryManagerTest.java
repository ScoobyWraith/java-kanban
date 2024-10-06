package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        taskManager = Managers.getDefault();
    }

    @Test
    public void testEmptyHistory() {
        Assertions.assertEquals(0, historyManager.getHistory().size(), "История не пуста");
    }

    @Test
    public void removeOldVersionOfTasksInHistory() {
        final Task task = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask subtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));

        // 0 - task,
        historyManager.add(task);
        // 0 - task, 1 - epic
        historyManager.add(epic);
        // 0 - task, 1 - epic, 2 - subtask
        historyManager.add(subtask);

        subtask.setDescription("New description of subtask");
        // 0 - task, 1 - epic, 2 - subtask
        historyManager.add(subtask);

        epic.setTitle("New title of epic");
        // 0 - task, 1 - subtask, 2 - epic
        historyManager.add(epic);

        task.setStatus(TaskStatus.DONE);
        // 0 - subtask, 1 - epic, 2 - task
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(
                subtask.getId(),
                history.get(0).getId(),
                "Неверный порядок истории для 1го элемента"
        );
        Assertions.assertEquals(
                epic.getId(),
                history.get(1).getId(),
                "Неверный порядок истории для 2го элемента"
        );
        Assertions.assertEquals(
                task.getId(),
                history.get(2).getId(),
                "Неверный порядок истории для 3го элемента"
        );
        Assertions.assertEquals(
                TaskStatus.NEW,
                history.get(0).getStatus(),
                "В истории неверный статус подзадачи"
        );
        Assertions.assertEquals(
                "New title of epic",
                history.get(1).getTitle(),
                "В истории неверное название эпика после обновления"
        );
        Assertions.assertEquals(
                "New description of subtask",
                history.get(0).getDescription(),
                "В истории неверное описание подзадачи после обновления"
        );
        Assertions.assertEquals(
                TaskStatus.DONE,
                history.get(2).getStatus(),
                "В истории неверный статус задачи после обновления"
        );
    }

    @Test
    public void checkRepeatsRemoving() {
        final Task task = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask subtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        epic.setTitle("New title");
        historyManager.add(epic);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(3, history.size(), "В истории не были удалены повторы");
    }

    @Test
    public void checkManualRemovingOfTasks() {
        final Task task1 = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Task task2 = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask subtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(task2.getId());
        historyManager.remove(subtask.getId());
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(1, history.size(), "Задачи из истории не были удалены");
        Assertions.assertEquals(epic.getId(), history.get(0).getId(), "Из истории были удалены не те задачи");
    }
}