package manager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String dataDelimiter = ",";
    private static final Charset charset = StandardCharsets.UTF_8;

    private final File fileWithData;
    private boolean availableSave;

    public FileBackedTaskManager(final File fileWithData) {
        super();
        this.fileWithData = fileWithData;
        availableSave = true;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (!file.exists()) {
            createDataFile(file);
        }

        int maxId = 0;
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        // Don't change file with data while creating new tasks over manager
        manager.setAvailableSave(false);

        try (BufferedReader br = new BufferedReader(new FileReader(file, charset))) {
            // skip first line with header
            if (br.ready()) {
                br.readLine();
            }

            while (br.ready()) {
                String row = br.readLine().trim();

                if (row.isEmpty()) {
                    continue;
                }

                Task task = fromString(row);

                switch (task.getType()) {
                    case TASK -> manager.addTask(task);

                    case EPIC -> manager.addEpic((Epic) task);

                    case SUBTASK -> manager.addSubtask((Subtask) task);

                    default -> throw new ManagerLoadException("Неизвестный тип задачи: " + task.getType());
                }

                maxId = Integer.max(maxId, task.getId());
            }
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при чтении FileBackedTaskManager из файла");
        }

        manager.idCounter = maxId;
        manager.setAvailableSave(true);
        return manager;
    }

    public boolean isAvailableSave() {
        return availableSave;
    }

    public void setAvailableSave(boolean availableSave) {
        this.availableSave = availableSave;
    }

    private static void createDataFile(File fileWithData) {
        try {
            Files.createFile(fileWithData.toPath());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при создании файла для FileBackedTaskManager");
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task createAndAddTask(Task task) {
        Task result = super.createAndAddTask(task);
        save();
        return result;
    }

    @Override
    public Subtask createAndAddSubtask(Subtask subtask) {
        Subtask result = super.createAndAddSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Epic createAndAddEpic(Epic epic) {
        Epic result = super.createAndAddEpic(epic);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    private static String getHeaderForDataFile() {
        return "id,type,title,status,description,epic";
    }

    private static String taskToString(Task task) {
        String result = String.join(dataDelimiter,
                task.getId().toString(),
                task.getType().toString(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription()
        );

        if (task.getType() == TaskType.SUBTASK) {
            result = String.join(dataDelimiter, result, ((Subtask) task).getEpicId().toString());
        }

        return result;
    }

    private static Task fromString(String value) {
        String[] cols = value.split(dataDelimiter);

        int id = Integer.parseInt(cols[0]);
        TaskType type = TaskType.valueOf(cols[1]);
        String title = cols[2];
        TaskStatus status = TaskStatus.valueOf(cols[3]);
        String description = cols[4];
        Task task;

        switch (type) {
            case TASK -> {
                task = new Task(title, description, status);
            }

            case EPIC -> {
                task = new Epic(title, description);
            }

            case SUBTASK -> {
                int epicId = Integer.parseInt(cols[5]);
                task = new Subtask(title, description, status, epicId);
            }

            default -> throw new ManagerLoadException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        return task;
    }

    public void save() {
        if (!isAvailableSave()) {
            return;
        }

        if (!fileWithData.exists()) {
            createDataFile(fileWithData);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileWithData, charset))) {
            int tasksQuantity = tasks.size() + subtasks.size() + epics.size();

            if (tasksQuantity > 0) {
                bw.write(getHeaderForDataFile() + "\n");
            }

            List<Map<Integer, ? extends Task>> maps = List.of(
                    tasks,
                    epics,
                    subtasks
            );

            for (Map<Integer, ? extends Task> map : maps) {
                for (Task task : map.values()) {
                    bw.write(taskToString(task) + "\n");
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении FileBackedTaskManager в файл");
        }
    }
}
