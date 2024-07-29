package model;

import java.util.HashMap;

public class TaskManager {
    private static int idCounter = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    // a. Получение списка всех задач [Task]
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    // a. Получение списка всех задач [Subtask]
    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    // a. Получение списка всех задач [Epic]
    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    // b. Удаление всех задач [Task]
    public void removeAllTasks() {
        tasks.clear();
    }

    // b. Удаление всех задач [Subtask]
    public void removeAllSubtasks() {
        subtasks.clear();
    }

    // b. Удаление всех задач [Epic]
    public void removeAllEpics() {
        epics.clear();
    }

    // c. Получение по идентификатору [Task]
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }

        System.out.println("Задачи с ИД " + id + " не найдено");
        return null;
    }

    // c. Получение по идентификатору [Subtask]
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }

        System.out.println("Позадачи с ИД " + id + " не найдено");
        return null;
    }

    // c. Получение по идентификатору [Epic]
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }

        System.out.println("Эпика с ИД " + id + " не найдено");
        return null;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Task]
    public Task createAndAddTask(Task task) {
        task.setId(createAndGetNewId());
        tasks.put(task.getId(), task);
        return task;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    public Subtask createAndAddSubtask(Subtask subtask) {
        subtask.setId(createAndGetNewId());
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    public Epic createAndAddEpic(Epic epic) {
        epic.setId(createAndGetNewId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Task]
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Subtask]
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();

        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Epic]
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    // f. Удаление по идентификатору [Task]
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return;
        }

        System.out.println("Задачи с ИД " + id + " не найдено для удаления");
    }

    // f. Удаление по идентификатору [Subtask]
    public void removeSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            return;
        }

        System.out.println("Подзадачи с ИД " + id + " не найдено для удаления");
    }

    // f. Удаление по идентификатору [Epic]
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            epics.remove(id);
            return;
        }

        System.out.println("Эпика с ИД " + id + " не найдено для удаления");
    }

    // 3 a. Получение списка всех подзадач определённого эпика
    public HashMap<Integer, Subtask> getAllSubtasksInEpic(Epic epic) {
        return epic.getSubtasks();
    }

    private static int createAndGetNewId() {
        return ++idCounter;
    }

    private void updateEpicStatus(Epic epic) {
        HashMap<Integer, Subtask> subtasks = epic.getSubtasks();

        if (subtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int subtasksWithStatusDone = 0;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.status == TaskStatus.DONE) {
                subtasksWithStatusDone++;
            }
        }

        if (subtasksWithStatusDone == subtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        epic.setStatus(TaskStatus.IN_PROGRESS);
    }
}
