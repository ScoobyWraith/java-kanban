package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    // a. Получение списка всех задач [Task]
    List<Task> getAllTasks();

    // a. Получение списка всех задач [Subtask]
    List<Subtask> getAllSubtasks();

    // a. Получение списка всех задач [Epic]
    List<Epic> getAllEpics();

    // b. Удаление всех задач [Task]
    void removeAllTasks();

    // b. Удаление всех задач [Subtask]
    void removeAllSubtasks();

    // b. Удаление всех задач [Epic]
    void removeAllEpics();

    // c. Получение по идентификатору [Task]
    Task getTaskById(int id);

    // c. Получение по идентификатору [Subtask]
    Subtask getSubtaskById(int id);

    // c. Получение по идентификатору [Epic]
    Epic getEpicById(int id);

    // d. Создание. Сам объект должен передаваться в качестве параметра [Task]
    Task createAndAddTask(Task task);

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    Subtask createAndAddSubtask(Subtask subtask);

    // d. Создание. Сам объект должен передаваться в качестве параметра [Subtask]
    Epic createAndAddEpic(Epic epic);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Task]
    void updateTask(Task task);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Subtask]
    void updateSubtask(Subtask subtask);

    // e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра [Epic]
    void updateEpic(Epic epic);

    // f. Удаление по идентификатору [Task]
    void removeTaskById(int id);

    // f. Удаление по идентификатору [Subtask]
    void removeSubtaskById(int id);

    // f. Удаление по идентификатору [Epic]
    void removeEpicById(int id);

    // 3 a. Получение списка всех подзадач определённого эпика
    List<Subtask> getAllSubtasksInEpic(int epicId);

    // Получить список из последних просмотренных задач
    List<Task> getHistory();
}
