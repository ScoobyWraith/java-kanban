package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private int idCounter = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    // a. Получение списка всех задач [Task]
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // a. Получение списка всех задач [Subtask]
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // a. Получение списка всех задач [Epic]
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // b. Удаление всех задач [Task]
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    // b. Удаление всех задач [Subtask]
    @Override
    public void removeAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearAllSubtaskIds();
            updateEpic(epic);
        }
    }

    // b. Удаление всех задач [Epic]
    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    // c. Получение по идентификатору [Task]
    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return task;
        }

        return null;
    }

    // c. Получение по идентификатору [Subtask]
    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        }

        return null;
    }

    // c. Получение по идентификатору [Epic]
    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epic;
        }

        return null;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Task]
    @Override
    public Task createAndAddTask(Task task) {
        task = new Task(task);
        task.setId(createAndGetNewTaskId());
        tasks.put(task.getId(), task);
        return task;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    @Override
    public Subtask createAndAddSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        subtask = new Subtask(subtask);
        subtask.setId(createAndGetNewTaskId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        return subtask;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    @Override
    public Epic createAndAddEpic(Epic epic) {
        epic = new Epic(epic);
        epic.setId(createAndGetNewTaskId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        return epic;
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Task]
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }

        tasks.put(task.getId(), task);
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Subtask]
    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask existedSubtask = subtasks.get(subtask.getId());

        if (existedSubtask.getEpicId() != subtask.getEpicId()) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpic(epic);
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Epic]
    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }

        Epic existedEpic = epics.get(epic.getId());
        existedEpic.setTitle(epic.getTitle());
        existedEpic.setDescription(epic.getDescription());
    }

    // f. Удаление по идентификатору [Task]
    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return;
        }

        System.out.println("Задачи с ИД " + id + " не найдено для удаления");
    }

    // f. Удаление по идентификатору [Subtask]
    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадачи с ИД " + id + " не найдены для удаления");
            return;
        }

        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    // f. Удаление по идентификатору [Epic]
    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпик с ИД " + id + " не найден для удаления");
            return;
        }

        Epic epic = epics.get(id);
        epics.remove(id);

        for (int subtaskId : epic.getAllSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }

    // 3 a. Получение списка всех подзадач определённого эпика
    @Override
    public ArrayList<Subtask> getAllSubtasksInEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с ИД " + epicId + " не найден для получения списка подзадач");
            return null;
        }

        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);

        for (int subtaskId : epic.getAllSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }

        return result;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int createAndGetNewTaskId() {
        return ++idCounter;
    }

    private void updateEpicStatus(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtaskIds = epic.getAllSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int subtasksWithStatusNew = 0;
        int subtasksWithStatusDone = 0;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);

            if (subtask.getStatus() == TaskStatus.NEW) {
                subtasksWithStatusNew++;
            }

            if (subtask.getStatus() == TaskStatus.DONE) {
                subtasksWithStatusDone++;
            }
        }

        if (subtasksWithStatusNew == subtaskIds.size()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        if (subtasksWithStatusDone == subtaskIds.size()) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        epic.setStatus(TaskStatus.IN_PROGRESS);
    }
}
