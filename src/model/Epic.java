package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.title, epic.description, epic.status);
        this.id = epic.id;
        subtaskIds = new ArrayList<>(epic.subtaskIds);
    }

    public ArrayList<Integer> getAllSubtaskIds() {
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

    public Epic getCopy() {
        return new Epic(this);
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
