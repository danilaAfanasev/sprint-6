package test.model;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    @DisplayName("Задачи с одинаковыми id должны быть идентичными")
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Купить хлеб", "В Пятерочке",10, Status.NEW, null, 100);
        Task task2 = new Task( "Купить колбасу", "В Пятерочке",10, Status.DONE,null, 100);
        assertEquals(task1, task2,
                "Ошибка, экземпляры класса Task должны быть равны друг другу, если у них одинаковые id");
    }
}