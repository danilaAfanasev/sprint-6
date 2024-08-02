package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, Status status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", id эпика='" + epicId + '}' + '\'';
    }
}
