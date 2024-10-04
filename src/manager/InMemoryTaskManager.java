package manager;

import exceptions.ManagerTaskTimeIntersection;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;
    protected final TreeSet<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    protected int idCounter = 0;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>((t1, t2) -> t1.getStartTime().get().compareTo(t2.getStartTime().get()));
    }

    // a. Получение списка всех задач [Task]
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // a. Получение списка всех задач [Subtask]
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // a. Получение списка всех задач [Epic]
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // b. Удаление всех задач [Task]
    @Override
    public void removeAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    // b. Удаление всех задач [Subtask]
    @Override
    public void removeAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();

        epics.values().forEach((Epic epic) -> {
            epic.clearAllSubtaskIds();
            updateEpic(epic);
        });
    }

    // b. Удаление всех задач [Epic]
    @Override
    public void removeAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        epics.clear();
        removeAllSubtasks();
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
        checkTasksTimeIntersection(task);
        task.setId(createAndGetNewTaskId());
        tasks.put(task.getId(), task);
        updateInPrioritizedTasks(task);
        return task;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    @Override
    public Subtask createAndAddSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        subtask = new Subtask(subtask);
        checkTasksTimeIntersection(subtask);
        subtask.setId(createAndGetNewTaskId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatusAndTimes(epic.getId());
        updateInPrioritizedTasks(subtask);
        return subtask;
    }

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    @Override
    public Epic createAndAddEpic(Epic epic) {
        epic = new Epic(epic);
        epic.setId(createAndGetNewTaskId());
        epics.put(epic.getId(), epic);
        updateEpicStatusAndTimes(epic.getId());
        return epic;
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Task]
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }

        checkTasksTimeIntersection(task);
        tasks.put(task.getId(), task);
        updateInPrioritizedTasks(task);
    }

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Subtask]
    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask existedSubtask = subtasks.get(subtask.getId());

        if (!existedSubtask.getEpicId().equals(subtask.getEpicId())) {
            return;
        }

        checkTasksTimeIntersection(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpic(epic);
        updateInPrioritizedTasks(subtask);
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
            historyManager.remove(id);
            removeFromPrioritizedTasks(id);
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
        historyManager.remove(id);
        removeFromPrioritizedTasks(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatusAndTimes(epic.getId());
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
        historyManager.remove(id);

        epic.getAllSubtaskIds().forEach((Integer subtaskId) -> {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
            removeFromPrioritizedTasks(id);
        });
    }

    // 3 a. Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getAllSubtasksInEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик с ИД " + epicId + " не найден для получения списка подзадач");
            return null;
        }

        Epic epic = epics.get(epicId);
        return epic.getAllSubtaskIds()
                .stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return Set.copyOf(this.prioritizedTasks);
    }

    protected void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        updateEpicStatusAndTimes(epic.getId());
    }

    protected void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatusAndTimes(epic.getId());
    }

    private void updateInPrioritizedTasks(Task task) {
        // remove old version from list
        removeFromPrioritizedTasks(task.getId());

        // add/update task in list
        if (!isTaskWithoutTimePeriod(task)) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(int id) {
        prioritizedTasks
                .stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .ifPresent(prioritizedTasks::remove);
    }

    private void checkTasksTimeIntersection(Task task) throws ManagerTaskTimeIntersection {
        if (isTaskWithoutTimePeriod(task)) {
            return;
        }

        prioritizedTasks
                .forEach((Task t) -> {
                    if (areTaskTimesIntersect(t, task)) {
                        throw new ManagerTaskTimeIntersection(
                                String.format("Задача %d накладывается по времени с задачей %d", task.getId(), t.getId())
                        );
                    }
                });
    }

    private boolean isTaskWithoutTimePeriod(Task task) {
        return task.getStartTime().isEmpty() || task.getEndTime().isEmpty();
    }

    private boolean areTaskTimesIntersect(Task t1, Task t2) {
        if (isTaskWithoutTimePeriod(t1) || isTaskWithoutTimePeriod(t2)) {
            return false;
        }

        // existing times was checked in isTaskWithoutTimePeriod()
        LocalDateTime startTask1 = t1.getStartTime().get();
        LocalDateTime startTask2 = t2.getStartTime().get();
        LocalDateTime endTask1 = t1.getEndTime().get();
        LocalDateTime endTask2 = t2.getEndTime().get();

        if (startTask1.equals(startTask2) || endTask1.equals(endTask2)) {
            return true;
        }

        return startTask1.isAfter(startTask2) && startTask1.isBefore(endTask2)
                || endTask1.isAfter(startTask2) && endTask1.isBefore(endTask2)
                || startTask2.isAfter(startTask1) && startTask2.isBefore(endTask1)
                || endTask2.isAfter(startTask1) && endTask2.isBefore(endTask1);
    }

    private int createAndGetNewTaskId() {
        return ++idCounter;
    }

    private void updateEpicStatusAndTimes(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getAllSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setDuration(null);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        int subtasksWithStatusNew = 0;
        int subtasksWithStatusDone = 0;
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        long epicDuration = 0;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            Optional<LocalDateTime> startTime = subtask.getStartTime();
            Optional<LocalDateTime> endTime = subtask.getEndTime();
            Optional<Duration> duration = subtask.getDuration();

            if (subtask.getStatus() == TaskStatus.NEW) {
                subtasksWithStatusNew++;
            }

            if (subtask.getStatus() == TaskStatus.DONE) {
                subtasksWithStatusDone++;
            }

            if (startTime.isPresent() && duration.isPresent() && endTime.isPresent()) {
                if (epicStartTime == null) {
                    epicStartTime = startTime.get();
                }

                if (epicEndTime == null) {
                    epicEndTime = endTime.get();
                }

                if (startTime.get().isBefore(epicStartTime)) {
                    epicStartTime = startTime.get();
                }

                if (endTime.get().isAfter(epicEndTime)) {
                    epicEndTime = endTime.get();
                }

                epicDuration += duration.get().toMinutes();
            }
        }

        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
        epic.setDuration(epicDuration == 0 ? null : Duration.ofMinutes(epicDuration));

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
