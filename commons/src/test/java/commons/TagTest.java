package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TagTest {

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag("Test Tag", "Red");
    }

    @Test
    void testConstructor() {
        Tag other = new Tag();
        assertNotNull(other);
    }

    @Test
    void testGetId() {
        assertEquals(0L, tag.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Test Tag", tag.getName());
    }

    @Test
    void testGetColor() {
        assertEquals("Red", tag.getColor());
    }

    @Test
    void testSetName() {
        String newName = "New Test Tag";
        tag.setName(newName);
        assertEquals(newName, tag.getName());
    }

    @Test
    void testSetColor() {
        String newColor = "Green";
        tag.setColor(newColor);
        assertEquals(newColor, tag.getColor());
    }

}
