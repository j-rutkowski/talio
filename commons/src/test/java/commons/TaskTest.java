package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Task 1");
    }

    @Test
    void defaultConstructor() {
        Task other = new Task();
        assertNotNull(other);
    }

    @Test
    void getId() {
        assertEquals(0, task.getId());
    }

    @Test
    void getTitle() {
        assertEquals("Task 1", task.getTitle());
    }

    @Test
    void setTitle() {
        task.setTitle("Task 2");
        assertEquals("Task 2", task.getTitle());
    }

    @Test
    void isStatus() {
        assertFalse(task.isStatus());
    }

    @Test
    void setStatus() {
        task.setStatus(true);
        assertTrue(task.isStatus());
    }

    @Test
    void testEquals() {
        Task other = new Task("Task 1");
        assertEquals(other, task);
    }

    @Test
    void testHashCode() {
        Task other = new Task("Task 1");
        assertEquals(other.hashCode(), task.hashCode());
        other.toString();

    }

//    @Test
//    void testToString() {
//        assertEquals("Task{id=0, title='Task 1', status=false}", task.toString());
//    }
}