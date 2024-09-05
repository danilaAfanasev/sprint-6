package controllers;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        TaskManager fileBackedTasksManager = Managers.getDefaultFile();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        Task task1 = new Task( "Задача", "description1", 1,Status.NEW, null, 1000);
        fileBackedTasksManager.addTask(task1);

        Epic epic2 = new Epic( "Эпик", "description2", 2, Status.NEW,
                LocalDateTime.parse("01.01.24 00:00",formatter),
                1000, LocalDateTime.parse("01.01.24 16:40",formatter));
        fileBackedTasksManager.addEpic(epic2);

        Subtask subtask3 = new Subtask( "Подзадача", "description3", 3, Status.NEW, 2,
                LocalDateTime.parse("01.01.24 00:00", formatter), 1000);
        fileBackedTasksManager.addSubtask(subtask3);

        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getEpicById(epic2.getId());
        fileBackedTasksManager.getSubtaskById(subtask3.getId());

        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(
                new File("sprint6/resources/file.csv"));
        for (Map.Entry<Integer, Task> entry : fileManager.tasks.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        for (Map.Entry<Integer, Epic> entry : fileManager.epics.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        for (Map.Entry<Integer, Subtask> entry : fileManager.subtasks.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println(fileManager.getHistory());
    }

    private final File File;
    private static final String FIRST_LINE = "id,type,name,status,description,startTime,endTime,duration,epic";

    public FileBackedTasksManager(File File) {
        this.File = File;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        Map<Integer, Task> fileHistory = new HashMap<>();
        List<Integer> idsHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> taskLines = reader.lines().toList();
            for (int i = 1; i < taskLines.size(); i++) {
                if (taskLines.get(i).isEmpty() && !taskLines.get(i + 1).isEmpty()) {
                    idsHistory = historyFromString(taskLines.get(i + 1));
                    break;
                }
                String[] line = taskLines.get(i).split(",");
                Task task = fromString(line);
                fileHistory.put(task.getId(), task);
                switch (task.getTaskType()) {
                    case TASK:
                        fileManager.tasks.put(task.getId(), task);
                        fileManager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        fileManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                        int epicId = ((Subtask) task).getEpicId();
                        List<Integer> subtaskIds = fileManager.epics.get(epicId).getSubtaskIds();
                        subtaskIds.add(task.getId());
                        fileManager.checkEpicStatus(epicId);
                        fileManager.prioritizedTasks.add(task);
                        break;
                }
                if (task.getId() > fileManager.nextID) {
                    fileManager.nextID = task.getId();
                }
            }
            for (Integer id : idsHistory) {
                fileManager.inMemoryHistoryManager.add(fileHistory.get(id));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла." + e.getMessage());
        }
        return fileManager;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> idsHistory = new ArrayList<>();
        String[] line = value.split(",");
        for (String id : line) {
            idsHistory.add(Integer.valueOf(id));
        }
        return idsHistory;
    }

    public static Task fromString(String[] line) {
        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String name = line[2];
        Status status = Status.valueOf(line[3]);
        String description = line[4];
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (!line[5].equals("null")) {
            startTime = LocalDateTime.parse(line[5], Task.formatter);
            endTime = LocalDateTime.parse(line[6], Task.formatter);
        } else {
            startTime = null;
            endTime = null;
        }
        long duration = Long.parseLong(line[7]);
        switch (taskType) {
            case TASK:
                return new Task(name, description, id, status, startTime, duration);
            case EPIC:
                return new Epic(name, description, id, status, startTime, duration, endTime);
            case SUBTASK:
                int epicId = Integer.parseInt(line[8]);
                return new Subtask(name, description, id, status, epicId, startTime, duration);
        }
        return null;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(File, StandardCharsets.UTF_8))) {
            writer.write(FIRST_LINE);
            writer.newLine();
            addTasksToFile(writer);
            writer.newLine();
            List<String> ids = new ArrayList<>();
            for (Task task : getHistory()) {
                ids.add(String.valueOf(task.getId()));
            }
            writer.write(String.join(",", ids));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач: " + e.getMessage());
        }
    }

    private void addTasksToFile(BufferedWriter writer) throws IOException {
        for (Task task : getListOfTasks()) {
            writer.write(toString(task));
            writer.newLine();
        }
        for (Epic epic : getListOfEpics()) {
            writer.write(toString(epic));
            writer.newLine();
        }
        for (Subtask subtask : getListOfSubtasks()) {
            writer.write(toString(subtask));
            writer.newLine();
        }
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + task.getStartTimeString() + "," + task.getEndTimeString() + "," +
                task.getDuration();
    }

    private String toString(Epic epic) {
        return epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() + "," +
                epic.getDescription() + "," + epic.getStartTimeString() + "," + epic.getEndTimeString() + "," +
                epic.getDuration();
    }

    private String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getName() + "," + subtask.getStatus() +
                "," + subtask.getDescription() + "," + subtask.getStartTimeString() + "," + subtask.getEndTimeString() +
                "," + subtask.getDuration() + "," + subtask.getEpicId();
    }

    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    @Override
    public void addSubtask(Subtask newSubtask) {
        super.addSubtask(newSubtask);
        save();
    }

    @Override
    public void addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskIdForRemove) {
        super.removeSubtaskById(subtaskIdForRemove);
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        super.updateSubtask(updateSubtask);
        save();
    }

    @Override
    public void checkEpicStatus(int epicId) {
        super.checkEpicStatus(epicId);
        save();
    }

    @Override
    public void setEpicDateTime(int epicId) {
        super.setEpicDateTime(epicId);
        save();
    }
}
