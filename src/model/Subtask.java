package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    protected final TaskType type = TaskType.SUBTASK;
    private final int epicId;

    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title,
                   String description,
                   TaskStatus status,
                   int epicId,
                   Duration duration,
                   LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(Subtask task) {
        super(task.title, task.description, task.status, task.duration, task.startTime);
        this.id = task.id;
        this.epicId = task.epicId;
    }

    @Override
    public void setId(int id) {
        if (id != epicId) {
            super.setId(id);
        }
    }

    public Integer getEpicId() {
        return epicId;
    }

    public TaskType getType() {
        return type;
    }

    public Subtask getCopy() {
        return new Subtask(this);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
