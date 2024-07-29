import model.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создайте две задачи, ...
        Task task1 = taskManager.createAndAddTask(new Task("Задача 1", "Описание заадчи 1"));
        Task task2 = taskManager.createAndAddTask(new Task("Задача 2", "Описание заадчи 2"));

        // ... а также эпик ...
        Epic grandEpic = taskManager.createAndAddEpic(new Epic("Большой эпик", "Описание большого эпика"));

        // ... с двумя подзадачами ...
        Subtask subtask1OfGrandEpic = taskManager
                .createAndAddSubtask(new Subtask("Позадача 1", "Описание подзадачи 1"), grandEpic);
        Subtask subtask2OfGrandEpic = taskManager
                .createAndAddSubtask(new Subtask("Позадача 2", "Описание подзадачи 2"), grandEpic);

        // ... и  эпик ...
        Epic epic = taskManager.createAndAddEpic(new Epic("Эпик", "Описание эпика"));

        // ... с одной подзадачей.
        Subtask subtask = taskManager
                .createAndAddSubtask(new Subtask("Позадача эпика", "Описание подзадачи эпика"), epic);

        // Распечатайте списки эпиков, задач и подзадач через System.out.println(..)
        System.out.println("Текущие спсики задач:");
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

        // И, наконец, попробуйте удалить одну из задач и один из эпиков
        taskManager.removeTaskById(task1.getId());
        taskManager.removeSubtaskById(subtask1OfGrandEpic.getId());
        taskManager.removeEpicById(epic.getId());

        System.out.println("Обновленные списки задач после удаления:");
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());


    }
}
