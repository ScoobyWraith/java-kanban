package model;

import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String title, String description, TaskStatus status) {
        super(title, description, status);
        subtasks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return new HashMap<>(subtasks);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.remove(subtask.getId());
            return;
        }

        System.out.println("В эпике с ИД " + id + " не найдена подзадача с ИД " + subtask.getId());
    }

    public void clearAllSubtasks() {
        this.subtasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks.size=" + subtasks.size() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
