package test.controllers;

import controllers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.*;
import exceptions.CollisionTaskException;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;
    private InMemoryTaskManager InMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    @DisplayName("Добавить новую задачу")
    void addNewTask() {
        final Task task = new Task("addNewTask", "addNewTask description");
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getListOfTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    @DisplayName("Добавить новые эпик и подзадачу")
    void addNewEpicAndSubtasks() {
        final Epic changeWallpaper = new Epic("Поменять обои",
                "Нужно успеть до Нового года");
        taskManager.addEpic(changeWallpaper);
        final Subtask changeWallpaperSubtaskFirst = new Subtask("Выбрать новые обои",
                "Минималистичные", changeWallpaper.getId());
        taskManager.addSubtask(changeWallpaperSubtaskFirst);
        final Subtask changeWallpaperSubtaskSecond = new Subtask("Поклеить новые обои",
                "Старые - выкинуть", changeWallpaper.getId());
        taskManager.addSubtask(changeWallpaperSubtaskSecond);
        final Subtask changeWallpaperSubtaskThird = new Subtask("Убедиться, что они подходят интерьеру",
                "Обои подходят мебели", changeWallpaper.getId());
        taskManager.addSubtask(changeWallpaperSubtaskThird);
        final Epic savedEpic = taskManager.getEpicById(changeWallpaper.getId());
        final Subtask savedSubtaskFirst = taskManager.getSubtaskById(changeWallpaperSubtaskFirst.getId());
        final Subtask savedSubtaskSecond = taskManager.getSubtaskById(changeWallpaperSubtaskSecond.getId());
        final Subtask savedSubtaskThird = taskManager.getSubtaskById(changeWallpaperSubtaskThird.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotNull(savedSubtaskSecond, "Подзадача не найдена.");
        assertEquals(changeWallpaper, savedEpic, "Эпики не совпадают.");
        assertEquals(changeWallpaperSubtaskFirst, savedSubtaskFirst, "Подзадачи не совпадают.");
        assertEquals(changeWallpaperSubtaskThird, savedSubtaskThird, "Подзадачи не совпадают.");

        final List<Epic> epics = taskManager.getListOfEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(changeWallpaper, epics.getFirst(), "Эпики не совпадают.");

        final List<Subtask> subtasks = taskManager.getListOfSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtaskFirst, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    @DisplayName("Обновить задачу с таким же id")
    public void updateTaskShouldReturnTaskWithTheSameId() {
        final Task expected = new Task("Имя", "Описание");
        taskManager.addTask(expected);
        final Task updatedTask = new Task( "Имя", "Описание", expected.getId(), Status.NEW);
        taskManager.updateTask(updatedTask);
        assertEquals(expected, updatedTask, "Вернулась задача с другим id");
    }

    @Test
    @DisplayName("Обновить эпик с таким же id")
    public void updateEpicShouldReturnEpicWithTheSameId() {
        final Epic expected = new Epic("Имя", "Описание");
        taskManager.addEpic(expected);
        final Epic updatedEpic = new Epic( "Новое имя", "Новое описание",expected.getId(),Status.NEW, Arrays.asList(1, 2, 3));
        taskManager.updateEpic(updatedEpic);
        assertEquals(expected, updatedEpic, "Вернулся эпик с другим id");
    }

    @Test
    @DisplayName("Обновить подзадачу с таким же id")
    public void updateSubtaskShouldReturnSubtaskWithTheSameId() {
        final Epic epic = new Epic("Имя", "Описание");
        taskManager.addEpic(epic);
        final Subtask expected = new Subtask("Имя", "Описание", epic.getId());
        taskManager.addSubtask(expected);
        final Subtask updatedSubtask = new Subtask( "Новое имя", "Новое описание",expected.getId(),
                Status.DONE, epic.getId());
        taskManager.updateSubtask(updatedSubtask);
        assertEquals(expected, updatedSubtask, "Вернулась подзадача с другим id");
    }

    @Test
    @DisplayName("Вернуть пустой список при удалении задач")
    public void deleteTasksShouldReturnEmptyList() {
        taskManager.addTask(new Task("Протереть пыль", "С новой, чистой тряпкой"));
        taskManager.addTask(new Task("Помыть полы", "С новым средством"));
        taskManager.removeAllTasks();
        List<Task> tasks = taskManager.getListOfTasks();
        assertTrue(tasks.isEmpty(), "После удаления задач список должен быть пуст");
    }

    @Test
    @DisplayName("Вернуть пустой список при удалении эпиков")
    public void deleteEpicsShouldReturnEmptyList() {
        taskManager.addEpic(new Epic("Поменять обои", "Нужно успеть до Нового года"));
        taskManager.removeAllEpics();
        List<Epic> epics = taskManager.getListOfEpics();
        assertTrue(epics.isEmpty(), "После удаления эпиков список должен быть пуст");
    }

    @Test
    @DisplayName("Вернуть пустой список при удалении подзадач")
    public void deleteSubtasksShouldReturnEmptyList() {
        Epic changeWallpaper = new Epic("Поменять обои", "Нужно успеть до Нового года");
        taskManager.addEpic(changeWallpaper);
        taskManager.addSubtask(new Subtask("Выбрать новые обои", "Минималистичные",
                changeWallpaper.getId()));
        taskManager.addSubtask(new Subtask("Поклеить новые обои", "Старые - выкинуть",
                changeWallpaper.getId()));
        taskManager.addSubtask(new Subtask("Убедиться, что они подходят интерьеру", "Обои подходят мебели",
                changeWallpaper.getId()));

        taskManager.removeAllSubtasks();
        List<Subtask> subtasks = taskManager.getListOfSubtasks();
        assertTrue(subtasks.isEmpty(), "После удаления подзадач список должен быть пуст");
    }

    @Test
    @DisplayName("Вернуть null при удалении задачи по id")
    public void deleteTaskByIdShouldReturnNullIfKeyIsMissing() {
        taskManager.addTask(new Task( "Протереть пыль", "С новой, чистой тряпкой", 1,Status.NEW));
        taskManager.addTask(new Task( "Помыть полы", "С новым средством",2, Status.DONE));
        taskManager.removeTaskById(3);
        assertNull(taskManager.getTaskById(3));
    }

    @Test
    @DisplayName("Вернуть null при удалении эпика по id")
    public void deleteEpicByIdShouldReturnNullIfKeyIsMissing() {
        taskManager.addEpic(new Epic( "Поменять обои", "Нужно успеть до Нового года", 1, Status.NEW, Arrays.asList(1, 2, 3)));
        taskManager.removeEpicById(1);
        taskManager.removeTaskById(1);
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    @DisplayName("Вернуть null при удалении подзадачи по id")
    public void deleteSubtaskByIdShouldReturnNullIfKeyIsMissing() {
        Epic changeWallpaper = new Epic("Поменять обои", "Нужно успеть до Нового года");
        taskManager.addEpic(changeWallpaper);
        taskManager.addSubtask(new Subtask("Выбрать новые обои", "Минималистичные",
                changeWallpaper.getId()));
        taskManager.addSubtask(new Subtask("Поклеить новые обои", "Старые - выкинуть",
                changeWallpaper.getId()));
        taskManager.addSubtask(new Subtask("Убедиться, что они подходят интерьеру", "Обои подходят мебели",
                changeWallpaper.getId()));
        assertNull(taskManager.getSubtaskById(5));
    }

    @Test
    @DisplayName("Иметь одинаковые параметры у добавленной и созданной задачи")
    void TaskCreatedAndTaskAddedShouldHaveSameVariables() {
        Task expected = new Task( "Протереть пыль", "С новой, чистой тряпкой",1, Status.DONE);
        taskManager.addTask(expected);

        List<Task> list = taskManager.getListOfTasks();
        Task actual = list.getFirst();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    @DisplayName("Проверить подзадачи со стаутсами NEW")
    public void testAllSubtasksNew() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, Status.NEW, Arrays.asList());
        taskManager.addEpic(epic1);

        taskManager.addSubtask(new Subtask("Подзадача 1", "Описание", 1, Status.NEW, epic1.getId()));
        taskManager.addSubtask(new Subtask("Подзадача 2", "Описание", 2, Status.NEW, epic1.getId()));

        taskManager.checkEpicStatus(epic1.getId());

        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    @DisplayName("Проверить подзадачи со стаутсами DONE")
    public void testAllSubtasksDone() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, Status.DONE, Arrays.asList());
        taskManager.addEpic(epic1);

        taskManager.addSubtask(new Subtask("Подзадача 1", "Описание", 1, Status.DONE, epic1.getId()));
        taskManager.addSubtask(new Subtask("Подзадача 2", "Описание", 2, Status.DONE, epic1.getId()));

        taskManager.checkEpicStatus(epic1.getId());

        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    @DisplayName("Проверить подзадачи со стаутсами NEW и DONE")
    public void testSubtasksMixedStatus() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, Status.NEW, Arrays.asList());
        taskManager.addEpic(epic1);

        taskManager.addSubtask(new Subtask("Подзадача 1", "Описание", 1, Status.NEW, epic1.getId()));
        taskManager.addSubtask(new Subtask("Подзадача 2", "Описание", 2, Status.DONE, epic1.getId()));

        taskManager.checkEpicStatus(epic1.getId());

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    @DisplayName("Проверить подзадачу со стаутсом IN_PROGRESS")
    public void testSubtasksInProgress() {
        Epic epic1 = new Epic("Эпик 1", "Описание 1", 1, Status.IN_PROGRESS, Arrays.asList());
        taskManager.addEpic(epic1);

        taskManager.addSubtask(new Subtask("Подзадача 1", "Описание", 1, Status.IN_PROGRESS, epic1.getId()));

        taskManager.checkEpicStatus(epic1.getId());

        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    @DisplayName("Проверить исключение на параллельное время задач")
    public void testValidateThrowsCollisionTaskException() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        Task existingTask = new Task("Купить хлеб", "В Пятерочке",1, Status.NEW, LocalDateTime.parse("01.01.24 00:00",formatter), 100);
        Task newTask = new Task("Купить хлеб", "В Пятерочке", 2, Status.NEW, LocalDateTime.parse("01.01.24 00:00",formatter), 100);

        taskManager.addTask(existingTask);

        assertThrows(CollisionTaskException.class, () -> {
            taskManager.validate(newTask);
        });
    }
}