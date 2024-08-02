package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, Status status, List<Integer> subtaskIds) {
        super(name, description, id, status);
        this.subtaskIds = subtaskIds;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", id подзадач(и)='" + subtaskIds + '}' + '\'';
    }
}