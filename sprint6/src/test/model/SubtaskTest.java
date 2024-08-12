package test;

import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void SubtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask( "Купить хлеб", "В Дикси у дома",5);
        Subtask subtask2 = new Subtask("Купить молоко", "В Пятерочке", 5);
        assertEquals(subtask1, subtask2,
                "Ошибка, наследники класса Task должны быть равны друг другу, если у них одинаковые id");
    }
}