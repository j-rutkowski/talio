package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    private Card card;
    private List<Task> tasks;
    private List<Tag> tags;

    @BeforeEach
    public void setUp() {
        card = new Card("Test Card", "Test Description");
        tasks = new ArrayList<>();
        tags = new ArrayList<>();
        tasks.add(new Task("Test Task"));
        tags.add(new Tag("Test Tag", "Red"));
    }

    @Test
    void testConstructors() {
        Card other = new Card();
        assertNotNull(other);
        other = new Card("Test Card", "Test Description", tasks);
        assertNotNull(other.getTasks());
        other = new Card("Test Card", "Test Description", new CardList(), tasks, tags);
        assertNotNull(other);
        other = new Card("Card", "desc", null);
        assertNotNull(other);
        other = new Card("Card", "desc", new CardList(), null, tags);
        assertNotNull(other);
    }

    @Test
    public void testGetId() {
        assertEquals(0L, card.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("Test Card", card.getTitle());
    }

    @Test
    public void testSetTitle() {
        card.setTitle("New Test Card");
        assertEquals("New Test Card", card.getTitle());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Test Description", card.getDescription());
    }

    @Test
    public void testSetDescription() {
        card.setDescription("New Test Description");
        assertEquals("New Test Description", card.getDescription());
    }

    @Test
    public void testGetTasks() {
        assertNotNull(card.getTasks());
        assertTrue(card.getTasks().isEmpty());
    }

    @Test
    public void testSetTasks() {
        card.setTasks(tasks);
        assertEquals(1, card.getTasks().size());
    }

    @Test
    public void testAddTask() {
        card.addTask(new Task("New Test Task"));
        assertEquals(1, card.getTasks().size());
    }

    @Test
    public void testGetTags() {
        assertNotNull(card.getTags());
    }

    @Test
    public void testSetTags() {
        card.setTags(tags);
        assertEquals(1, card.getTags().size());
    }

    @Test
    public void testAddTag() {
        card.setTags(tags);
        card.addTag(new Tag("New Test Tag", "Red"));
        assertEquals(2, card.getTags().size());
    }

    @Test
    public void testEquals() {
        Card card1 = new Card("Test Card", "Test Description");
        Card card2 = new Card("Test Card", "Test Description");
        assertEquals(card1, card2);
    }

    @Test
    public void testHashCode() {
        Card card1 = new Card("Test Card", "Test Description");
        Card card2 = new Card("Test Card", "Test Description");
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    public void testToString() {
        assertNotNull(card.toString());
    }

    @Test
    void updateTask() {
        card.setTasks(tasks);
        Task task = new Task("New Test Task");
        card.updateTask(task, 0);
        assertEquals(task, card.getTasks().get(0));
    }

    @Test
    void updateTag() {
        card.setTags(tags);
        Tag tag = new Tag("New Test Tag", "Red");
        card.updateTag(tag, 0);
        assertEquals(tag, card.getTags().get(0));
    }
}
