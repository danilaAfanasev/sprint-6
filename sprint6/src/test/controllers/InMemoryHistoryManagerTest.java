package test.controllers;

import controllers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.*;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    @DisplayName("Сохранить старую задачу после обновления в историю")
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task wipeDust = new Task("Протереть пыль", "С новой, чистой тряпкой");
        taskManager.addTask(wipeDust);
        taskManager.getTaskById(wipeDust.getId());
        taskManager.updateTask(new Task( "Не забыть протереть пыль",
                "Можно использовать старую тряпку, но лучше новую",wipeDust.getId(), Status.IN_PROGRESS));

        List<Task> tasks = taskManager.getHistory();
        Task oldTask = tasks.getFirst();
        assertEquals(wipeDust.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");
        assertEquals(wipeDust.getDescription(), oldTask.getDescription(),
                "В истории не сохранилась старая версия задачи");
    }

    @Test
    @DisplayName("Сохранить старый эпик после обновления в историю")
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        Epic changeWallpaper = new Epic("Поменять обои", "Нужно успеть до Нового года");
        taskManager.addEpic(changeWallpaper);
        taskManager.getEpicById(changeWallpaper.getId());
        taskManager.updateEpic(new Epic( "Имя", "Описание",changeWallpaper.getId(), Status.IN_PROGRESS, Arrays.asList(1, 2, 3)));

        List<Task> epics = taskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(changeWallpaper.getName(), oldEpic.getName(),
                "В истории не сохранилась старая версия эпика");
        assertEquals(changeWallpaper.getDescription(), oldEpic.getDescription(),
                "В истории не сохранилась старая версия эпика");
    }

    @Test
    @DisplayName("Сохранить старую подзадачу после обновления в историю")
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        Epic changeWallpaper = new Epic("Поменять обои", "Нужно успеть до Нового года");
        taskManager.addEpic(changeWallpaper);
        Subtask changeWallpaperSubtaskFirst = new Subtask("Выбрать новые обои", "Минималистичные",
                changeWallpaper.getId());
        taskManager.addSubtask(changeWallpaperSubtaskFirst);
        taskManager.getSubtaskById(changeWallpaperSubtaskFirst.getId());
        taskManager.updateSubtask(new Subtask( "Имя",
                "Описание",changeWallpaperSubtaskFirst.getId(), Status.IN_PROGRESS, changeWallpaper.getId()));

        List<Task> subtasks = taskManager.getHistory();
        Subtask oldSubtask = (Subtask) subtasks.getFirst();
        assertEquals(changeWallpaperSubtaskFirst.getName(), oldSubtask.getName(),
                "В истории не сохранилась старая версия эпика");
        assertEquals(changeWallpaperSubtaskFirst.getDescription(), oldSubtask.getDescription(),
                "В истории не сохранилась старая версия эпика");
    }

    @Test
    @DisplayName("Добавить задачу в историю")
    public void addTaskToHistory() {
        Task task1 = new Task("Первая задача", "Описание 1", 1, Status.NEW);
        Task task2 = new Task("Вторая задача", "Описание 2", 2, Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории должен быть равен 2 после добавления 2 задач");
        assertTrue(history.contains(task1), "В истории должна быть первая задача");
        assertTrue(history.contains(task2), "В истории должна быть вторая задача");
    }

    @Test
    @DisplayName("Удалить задачу из истории")
    public void removeTaskFromHistory() {
        Task task1 = new Task("Задача для удаления", "Описание", 1, Status.NEW);
        Task task2 = new Task("Другая задача", "Описание", 2, Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Размер истории должен быть равен 1 после удаления задачи");
        assertFalse(history.contains(task1), "История не должна содержать удаленную задачу");
        assertTrue(history.contains(task2), "В истории должна быть другая задача");
    }

    @Test
    @DisplayName("Добавить одинаковую задачу в историю")
    public void addingSameTaskUpdatesHistory() {
        Task task1 = new Task("Одинаковая задача", "Описание", 1, Status.NEW);

        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Размер истории должен быть равен 1 после добавления идентичной задачи");
        assertTrue(history.contains(task1), "История должна содержать только одну задачу");
    }

    @Test
    @DisplayName("Удалить несуществующую задачу")
    public void removeNonExistentTask() {
        Task task1 = new Task("Задача", "Описание", 1, Status.NEW);

        historyManager.add(task1);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "Размер истории должен быть равен 1 после удаления несуществующей задачи");
        assertTrue(history.contains(task1), "История должна содержать существующую задачу");
    }

    @Test
    @DisplayName("Проверить несколько операций")
    public void testMultipleOperations() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, Status.NEW);
        Task task2 = new Task("Задача 2", "Задача 2", 2, Status.NEW);
        Task task3 = new Task("Задача 3", "Описание 3", 3, Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Размер истории должен быть равен 2 после удаления задачи");
        assertFalse(history.contains(task2), "История не должна содержать удаленную задачу");
        assertTrue(history.contains(task1), "История должна содержать задачу 1");
        assertTrue(history.contains(task3), "История должна содержать задачу 3");
    }

    @Test
    @DisplayName("Проверить пустую историю задач")
    public void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    @DisplayName("Проверить дублирование")
    public void testDuplicateTasks() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, Status.NEW);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("Проверить удаление из истории: начало")
    public void testRemoveFromHistoryStart() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, Status.NEW);
        Task task2 = new Task("Задача 2", "Задача 2", 2, Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        assertFalse(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task2));
    }

    @Test
    @DisplayName("Проверить удаление из истории: середина")
    public void testRemoveFromHistoryMiddle() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, Status.NEW);
        Task task2 = new Task("Задача 2", "Задача 2", 2, Status.NEW);
        Task task3 = new Task("Задача 3", "Описание 3", 3, Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertFalse(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(task1));
        assertTrue(historyManager.getHistory().contains(task3));
    }

    @Test
    @DisplayName("Проверить удаление из истории: конец")
    public void testRemoveFromHistoryEnd() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, Status.NEW);
        Task task2 = new Task("Задача 2", "Задача 2", 2, Status.NEW);
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        assertFalse(historyManager.getHistory().contains(task2));
        assertTrue(historyManager.getHistory().contains(task1));
    }
}