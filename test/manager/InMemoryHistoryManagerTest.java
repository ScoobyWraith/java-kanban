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

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void oldVersionOfTasksInHistory() {
        TaskManager taskManager = Managers.getDefault();
        final Task task = taskManager.createAndAddTask(new Task("a", "b", TaskStatus.NEW));
        final Epic epic = taskManager.createAndAddEpic(new Epic("a", "b"));
        final Subtask subtask
                = taskManager.createAndAddSubtask(new Subtask("a", "b", TaskStatus.NEW, epic.getId()));

        // row 0
        historyManager.add(task);
        // row 1
        historyManager.add(epic);
        // row 2
        historyManager.add(subtask);

        subtask.setDescription("New description of subtask");
        // row 3
        historyManager.add(subtask);

        epic.setTitle("New title of epic");
        // row 4
        historyManager.add(epic);

        task.setStatus(TaskStatus.DONE);
        // row 5
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(history.get(0).getStatus(), TaskStatus.NEW, "В истории неверный статус задачи");
        Assertions.assertEquals(history.get(1).getTitle(), "a", "В истории неверное название эпика");
        Assertions.assertEquals(
                history.get(2).getDescription(),
                "b",
                "В истории неверное описание подзадачи"
        );
        Assertions.assertEquals(
                history.get(3).getDescription(),
                "New description of subtask",
                "В истории неверное описание подзадачи после обновления"
        );
        Assertions.assertEquals(
                history.get(4).getTitle(),
                "New title of epic",
                "В истории неверное название эпика после обновления"
        );
        Assertions.assertEquals(
                history.get(5).getStatus(),
                TaskStatus.DONE,
                "В истории неверный статус задачи после обновления"
        );
    }

    @Test
    public void addTasksOverSize() {
        final int maxSize = 10;
        final int testSize = maxSize + 5;

        for (int i = 0; i < testSize; i++) {
            Task task = new Task(String.valueOf(i), "b", TaskStatus.NEW);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(history.size(), maxSize, "Неправильный размер истории после переполнения");
        Assertions.assertEquals(
                history.get(0).getTitle(),
                String.valueOf(testSize - maxSize),
                "Неправильный сдвиг в истории для первого элемента"
        );
        Assertions.assertEquals(
                history.get(maxSize - 1).getTitle(),
                String.valueOf(testSize - 1),
                "Неправильный сдвиг в истории для последнего элемента"
        );
    }
}