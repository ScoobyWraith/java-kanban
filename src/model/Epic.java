package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getAllSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int id) {
        if (subtaskIds.contains(id)) {
            return;
        }

        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        if (!subtaskIds.contains(id)) {
            return;
        }

        subtaskIds.remove(id);
    }

    public void clearAllSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks.size=" + subtaskIds.size() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
