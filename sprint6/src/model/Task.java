package model;

import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status = Status.NEW;
    protected LocalDateTime startTime;
    protected long duration;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, int id, Status status, LocalDateTime startTime,
                long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        if (startTime == null) {
            return "null";
        }
        return startTime.format(formatter);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        }
        return null;
    }

    public String getEndTimeString() {
        if (startTime != null) {
            return getEndTime().format(formatter);
        }
        return null;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                (id == task.id) &&
                Objects.equals(status, task.status) &&
                Objects.equals(startTime, task.startTime) &&
                (duration == task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Задача{" +
                "название='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", дата начала='" + getStartTimeString() + '\'' +
                ", продолжительность='" + duration + '}' + '\'';
    }
}