package test;

import controllers.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultShouldInitializeInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertTrue(taskManager instanceof InMemoryTaskManager);
    }

    @Test
    void getDefaultHistoryShouldInitializeInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }
}