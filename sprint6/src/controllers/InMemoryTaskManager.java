package controllers;

import model.Epic;
import model.Status;
import model.Task;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    protected int nextID = 0;

    @Override
    public void addTask(Task newTask) {
        int taskId = ++nextID;
        newTask.setId(taskId);
        tasks.put(taskId, newTask);
    }

    @Override
    public void addSubtask(Subtask newSubtask) {
        int newSubtaskId = ++nextID;
        newSubtask.setId(newSubtaskId);
        subtasks.put(newSubtaskId, newSubtask);
        int epicId = newSubtask.getEpicId();
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        subtaskIds.add(newSubtaskId);
        checkEpicStatus(epicId);
    }

    @Override
    public void addEpic(Epic newEpic) {
        int epicId = ++nextID;
        newEpic.setId(epicId);
        epics.put(epicId, newEpic);
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            checkEpicStatus(epic.getId());
        }
        for (Integer id : subtasks.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            int epicId = epic.getId();
            List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
                inMemoryHistoryManager.remove(subtaskId);
            }
        }
        for (Integer id : epics.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public List<Task> getListOfTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null) {
            inMemoryHistoryManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            inMemoryHistoryManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.get(id) != null) {
            inMemoryHistoryManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public List<Subtask> getListOfSubtasksByOneEpic(int id) {
        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        List<Subtask> subtasksByOneEpic = new ArrayList<>();
        for (int subtaskId : subtaskIds) {
            subtasksByOneEpic.add(subtasks.get(subtaskId));
        }
        return subtasksByOneEpic;
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void removeEpicById(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            subtasks.remove(subtaskId);
            inMemoryHistoryManager.remove(subtaskId);
        }
        epics.remove(epicId);
        inMemoryHistoryManager.remove(epicId);
    }

    @Override
    public void removeSubtaskById(int subtaskIdForRemove) {
        int epicId = subtasks.get(subtaskIdForRemove).getEpicId();
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        subtaskIds.remove((Integer) subtaskIdForRemove);
        subtasks.remove(subtaskIdForRemove);
        checkEpicStatus(epicId);
        inMemoryHistoryManager.remove(subtaskIdForRemove);
    }

    @Override
    public void updateTask(Task updateTask) {
        tasks.put(updateTask.getId(), updateTask);
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        epics.put(updateEpic.getId(), updateEpic);
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        subtasks.put(updateSubtask.getId(), updateSubtask);
        int epicId = subtasks.get(updateSubtask.getId()).getEpicId();
        checkEpicStatus(epicId);
    }

    @Override
    public void checkEpicStatus(int epicId) {
        int counterNEW = 0;
        int counterDONE = 0;
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            if (subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                counterNEW++;
            } else if (subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                counterDONE++;
            }
        }
        if (subtaskIds.size() == counterNEW || subtaskIds.isEmpty()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (subtaskIds.size() == counterDONE) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}