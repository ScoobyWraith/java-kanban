import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создайте две задачи, ...
        Task task1 = taskManager.createAndAddTask(new Task("Задача 1", "Описание", TaskStatus.NEW));
        Task task2 = taskManager.createAndAddTask(new Task("Задача 2", "Описание", TaskStatus.NEW));

        // ... а также эпик ...
        Epic grandEpic = taskManager
                .createAndAddEpic(new Epic("Большой эпик", "Описание"));

        // ... с двумя подзадачами ...
        Subtask subtask1OfGrandEpic = taskManager
                .createAndAddSubtask(new Subtask("Подзадача 1", "Описание", TaskStatus.NEW, grandEpic.getId()));
        Subtask subtask2OfGrandEpic = taskManager
                .createAndAddSubtask(new Subtask("Подзадача 2", "Описание", TaskStatus.NEW, grandEpic.getId()));

        // ... и  эпик ...
        Epic epic = taskManager.createAndAddEpic(new Epic("Эпик", "Описание эпика"));

        // ... с одной подзадачей.
        Subtask subtask = taskManager
                .createAndAddSubtask(new Subtask("Подзадача эпика", "Описание", TaskStatus.NEW, epic.getId()));

        // Распечатайте списки эпиков, задач и подзадач через System.out.println(..)
        System.out.println("Текущие списки задач:");
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        // Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился,
        // а статус эпика рассчитался по статусам подзадач
        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        task2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task2);

        subtask1OfGrandEpic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1OfGrandEpic);

        subtask2OfGrandEpic.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2OfGrandEpic);

        grandEpic.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(grandEpic);

        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        System.out.println("Обновленные списки задач с нвоыми статусами:");
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        // Вывести историю просмотра задач
        System.out.println("История просмотра задач:");
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getEpicById(epic.getId());
        System.out.println(taskManager.getHistory());

        // И, наконец, попробуйте удалить одну из задач и один из эпиков
        taskManager.removeTaskById(task1.getId());
        taskManager.removeSubtaskById(subtask1OfGrandEpic.getId());
        taskManager.removeEpicById(epic.getId());

        System.out.println("Обновленные списки задач после удаления:");
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        // Опциональный пользовательский сценарий [Спринт 6]
        System.out.println("Опциональный пользовательский сценарий [Спринт 6]");
        taskManager = Managers.getDefault();
        // две задачи
        Task t1 = taskManager.createAndAddTask(new Task("t1t", "t1d", TaskStatus.NEW));
        Task t2 = taskManager.createAndAddTask(new Task("t2t", "t2d", TaskStatus.NEW));

        // эпик с тремя подзадачами
        Epic e1 = taskManager.createAndAddEpic(new Epic("e1t", "e1d"));
        Subtask epic1subtask1 = taskManager
                .createAndAddSubtask(new Subtask("e1s1t", "e1s1d", TaskStatus.NEW, e1.getId()));
        Subtask epic1subtask2 = taskManager
                .createAndAddSubtask(new Subtask("e1s2t", "e1s2d", TaskStatus.NEW, e1.getId()));
        Subtask epic1subtask3 = taskManager
                .createAndAddSubtask(new Subtask("e1s3t", "e1s3d", TaskStatus.NEW, e1.getId()));

        // эпик без подзадач
        Epic e2 = taskManager.createAndAddEpic(new Epic("e2t", "e2d"));

        // Запросите созданные задачи несколько раз в разном порядке.
        // После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        taskManager.getTaskById(t2.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(t1.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(epic1subtask2.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(t1.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getSubtaskById(epic1subtask2.getId());
        System.out.println(taskManager.getHistory());

        // Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться
        taskManager.removeTaskById(t2.getId());
        System.out.println(taskManager.getHistory());

        // Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        taskManager.removeEpicById(e1.getId());
        System.out.println(taskManager.getHistory());
    }
}
