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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private static void createDataFile(File fileWithData) {
        try {
            Files.createFile(fileWithData.toPath());
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при создании файла для FileBackedTaskManager");
        }
    }

    private static String getHeaderForDataFile() {
        return "id,type,title,status,description,startTime,duration,epic";
    }

    private static String taskToString(Task task) {
        Duration duration = task.getDuration().orElse(Duration.ofMinutes(0));
        Optional<LocalDateTime> startTime = task.getStartTime();

        String result = String.join(dataDelimiter,
                task.getId().toString(),
                task.getType().toString(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription(),
                startTime.map(LocalDateTime::toString).orElse(" "),
                Long.toString(duration.toMinutes())
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
        LocalDateTime startTime = cols[5].isBlank() ? null : LocalDateTime.parse(cols[5]);
        long durationTime = Long.parseLong(cols[6]);
        Duration duration = durationTime == 0 ? null : Duration.ofMinutes(durationTime);

        Task task;

        switch (type) {
            case TASK -> {
                task = new Task(title, description, status, duration, startTime);
            }

            case EPIC -> {
                task = new Epic(title, description);
            }

            case SUBTASK -> {
                int epicId = Integer.parseInt(cols[7]);
                task = new Subtask(title, description, status, epicId, duration, startTime);
            }

            default -> throw new ManagerLoadException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        return task;
    }

    public boolean isAvailableSave() {
        return availableSave;
    }

    public void setAvailableSave(boolean availableSave) {
        this.availableSave = availableSave;
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
    public Task createTask(Task task) {
        Task result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic result = super.createEpic(epic);
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
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
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
