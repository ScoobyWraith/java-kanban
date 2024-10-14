package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    protected final TaskType type = TaskType.EPIC;
    private final List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.title, epic.description, epic.status, epic.duration, epic.startTime);
        this.id = epic.id;
        this.endTime = epic.endTime;
        subtaskIds = new ArrayList<>(epic.subtaskIds);
    }

    public List<Integer> getAllSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int id) {
        if (subtaskIds.contains(id) || id == this.id) {
            return;
        }

        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        if (!subtaskIds.contains(id)) {
            return;
        }

        subtaskIds.remove(Integer.valueOf(id));
    }

    public void clearAllSubtaskIds() {
        subtaskIds.clear();
    }

    public TaskType getType() {
        return type;
    }

    public Epic getCopy() {
        return new Epic(this);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(this.endTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", subtasks.size=" + subtaskIds.size() +
                ", status=" + status +
                '}';
    }
}
