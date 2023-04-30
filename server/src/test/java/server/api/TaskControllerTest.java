package server.api;

import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskControllerTest {

    private TaskRepository repo;
    private TaskController controller;

    @BeforeEach
    void setUp() {
        repo = mock(TaskRepository.class);
        controller = new TaskController(repo);
    }

    @Test
    void testAddTask() {
        Task task = new Task("Task name");
        when(repo.save(task)).thenReturn(task);

        ResponseEntity<Task> response = controller.addTask(task);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }

    @Test
    void testEditStatus() {
        boolean newStatus = true;

        Task task = new Task("Task name");

        when(repo.existsById(0L)).thenReturn(true);
        when(repo.getById(0L)).thenReturn(task);
        when(repo.save(task)).thenReturn(task);

        ResponseEntity<Task> response = controller.editStatus(newStatus, 0L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newStatus, task.isStatus());
    }

    @Test
    void testEditStatusWithInvalidId() {
        long invalidTaskId = -1L;

        when(repo.existsById(invalidTaskId)).thenReturn(false);

        ResponseEntity<Task> response = controller.editStatus(true, invalidTaskId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}