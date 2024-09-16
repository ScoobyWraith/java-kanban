package manager;

import java.io.File;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedManager(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }
}
