package test;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Купить хлеб", "В Пятерочке",10, Status.NEW);
        Task task2 = new Task( "Купить колбасу", "В Пятерочке",10, Status.DONE);
        assertEquals(task1, task2,
                "Ошибка, экземпляры класса Task должны быть равны друг другу, если у них одинаковые id");
    }
}