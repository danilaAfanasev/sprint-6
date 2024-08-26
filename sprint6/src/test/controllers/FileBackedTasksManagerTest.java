package test.controllers;

import controllers.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import model.*;
import exceptions.ManagerSaveException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest {
    private FileBackedTasksManager fileBackedTasksManager;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("test_file", ".csv");
        tempFile.deleteOnExit();
        fileBackedTasksManager = new FileBackedTasksManager(tempFile);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    @DisplayName("Сохранение и загрузка из пустого файла")
    public void testLoadFromEmptyFile() {
        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(tempFile);
        fileBackedTasksManager.save();
        assertNotNull(loadedManager);
        assertTrue(loadedManager.getListOfTasks().isEmpty());
        assertTrue(loadedManager.getListOfEpics().isEmpty());
        assertTrue(loadedManager.getListOfSubtasks().isEmpty());
    }

    @Test
    @DisplayName("Сохранение нескольких задач")
    public void testSaveMultipleTasks() throws IOException {
        fileBackedTasksManager.addTask(new Task("Task1", "Description1", 1, Status.NEW));
        fileBackedTasksManager.addTask(new Epic("Epic1", "Description2", 2, Status.NEW));
        fileBackedTasksManager.addTask(new Subtask("Subtask1", "Description3", 3, Status.NEW, 2));

        fileBackedTasksManager.save();

        List<String> lines = Files.readAllLines(tempFile.toPath(), StandardCharsets.UTF_8);
        assertEquals(5, lines.size());
        assertTrue(lines.get(1).contains("Task1"));
        assertTrue(lines.get(2).contains("Epic1"));
        assertTrue(lines.get(3).contains("Subtask1"));
    }

    @Test
    @DisplayName("Загрузка нескольких задач")
    public void testLoadMultipleTasks() throws IOException {
        String content = """
                id,type,name,status,description,startTime,endTime,duration,epic
                1,TASK,Задача,NEW,description1,null,null,1000
                2,EPIC,Эпик,NEW,description2,01.01.24 00:00,01.01.24 16:40,1000
                3,SUBTASK,Подзадача,NEW,description3,01.01.24 00:00,01.01.24 16:40,1000,2

                1,2,3""";
        Files.writeString(tempFile.toPath(), content);

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(tempFile);

        assertNotNull(loadedManager);
        assertEquals(1, loadedManager.getListOfTasks().size());
        assertEquals(3, loadedManager.getHistory().size());
    }

    @Test
    @DisplayName("Проверить исключение")
    public void testLoadFromFileThrowsManagerSaveException() {
        File nonExistentFile = new File("non_existent_file.csv");

        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTasksManager.loadFromFile(nonExistentFile);
        });
    }
}
