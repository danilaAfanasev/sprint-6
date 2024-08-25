package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, Status status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, Status status, int epicId, LocalDateTime startTime,
                   long duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(name, subtask.name) &&
                Objects.equals(description, subtask.description) &&
                (id == subtask.id) &&
                Objects.equals(status, subtask.status) &&
                (epicId == subtask.epicId) &&
                Objects.equals(startTime, subtask.startTime) &&
                (duration == subtask.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, startTime, duration, epicId);
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", дата начала='" + getStartTimeString() + '\'' +
                ", продолжительность='" + duration + '\'' +
                ", id эпика='" + epicId + '}' + '\'';
    }
}
