package controllers;

import java.io.File;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile() {
        return new FileBackedTasksManager(new File("sprint6/resources/file.csv"));
    }
}