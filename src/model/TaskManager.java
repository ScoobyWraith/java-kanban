package model;

import java.util.HashMap;

public class TaskManager {
    private static int idCounter = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    private static int createAndGetNewId() {
        return ++idCounter;
    }


}
