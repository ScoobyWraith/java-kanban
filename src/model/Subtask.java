package model;

public class Subtask extends Task {
    protected final TaskType type = TaskType.SUBTASK;
    private final int epicId;

    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask task) {
        super(task.title, task.description, task.status);
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
