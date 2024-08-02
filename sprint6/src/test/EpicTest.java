package test;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

class EpicTest {

    @Test
    public void EpicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "Описание 1", 1, Status.NEW, Arrays.asList(1, 2, 3));
        Epic epic2 = new Epic("Epic 2", "Описание 2", 1, Status.NEW, Arrays.asList(4, 5, 6));
        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }
}