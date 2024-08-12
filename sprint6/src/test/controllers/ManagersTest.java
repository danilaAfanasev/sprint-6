package test.controllers;

import controllers.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    @DisplayName("Должен инициализировать InMemoryTaskManager по умолчанию")
    void getDefaultShouldInitializeInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    @DisplayName("Должен инициализировать InMemoryTaskManager по умолчанию")
    void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }
}