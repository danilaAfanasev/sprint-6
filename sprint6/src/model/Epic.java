package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public Epic(String name, String description, int id, Status status, List<Integer> subtaskIds) {
        super(name, description, id, status);
        this.subtaskIds = subtaskIds != null ? new ArrayList<>(subtaskIds) : new ArrayList<>();
    }

    public Epic(String name, String description, int id, Status status, LocalDateTime startTime, long duration,
                LocalDateTime endTime) {
        super(name, description, id, status, startTime, duration);
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String getEndTimeString() {
        if (endTime == null) {
            return "null";
        }
        return getEndTime().format(formatter);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return id == epic.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Эпик{" +
                    "название='" + name + '\'' +
                    ", описание='" + description + '\'' +
                    ", id='" + id + '\'' +
                    ", статус='" + status + '\'' +
                    ", дата начала='" + getStartTimeString() + '\'' +
                    ", продолжительность='" + duration + '\'' +
                    ", id подзадач(и)='" + subtaskIds + '}' + '\'';
        }
    }
