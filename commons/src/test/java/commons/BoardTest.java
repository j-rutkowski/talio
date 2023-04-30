package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board testBoard;

    @BeforeEach
    void init(){
        testBoard = new Board("Epic title", null);
    }

    @Test
    void constructorTest(){
        Board board = new Board();

        assertNotNull(board);
    }

    @Test
    void testGetId() {
        assertEquals(0L, testBoard.getId());
    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag("Test Tag", "Red");
        testBoard.addTag(tag);

        assertEquals(1, testBoard.getTags().size());
        assertEquals(tag, testBoard.getTags().get(0));
    }

    @Test
    public void testSetColor() {
        String color = "0x000000ff";
        testBoard.setColor(color);

        assertEquals(color, testBoard.getColor());
    }

    @Test
    void getTitleTest(){
        String res = "Epic title";

        assertEquals(testBoard.getTitle(), res);
    }

    @Test
    void setTitleTest(){
        String newTitle = "newTitle";
        testBoard.setTitle(newTitle);
        assertEquals(newTitle, testBoard.getTitle());
    }

    @Test
    void getCardListTest(){
        Card card = new Card("card1", null);
        CardList cardList = new CardList("my list", null);
        cardList.addCard(card);

        List<CardList> list = new ArrayList<>();
        list.add(cardList);

        Board board = new Board("board", list);
        assertEquals(list, board.getCardLists());
    }

    @Test
    void setCardListTest() {
        Card card = new Card("card3", null);
        CardList list = new CardList("my list", null);
        list.addCard(card);

        testBoard.addCardList(list);

        assertTrue(testBoard.getCardLists().contains(list));
    }

    @Test
    void equalsTest(){
        Board board2 = new Board("Epic title", null);

        assertEquals(testBoard, board2);
    }

    @Test
    void hashTest(){
        Board board2 = new Board("Epic title", null);
        assertEquals(testBoard.hashCode(), board2.hashCode());
    }


    @Test
    void getKey() {
        testBoard.setKey("key");
        assertEquals("key", testBoard.getKey());
    }

    @Test
    void setKey() {
        testBoard.setKey("key");
        assertEquals("key", testBoard.getKey());
    }

    @Test
    public void testToString() {
        assertEquals("Board{id=0, cardLists=[], tags=[], title='Epic title', color='0xcacacaff', key='null'}", testBoard.toString());
    }

    @Test
    public void passwordTest(){
        assertFalse(testBoard.hasPassword());
        testBoard.setPassword("123456");
        assertTrue(testBoard.hasPassword());
        assertEquals("123456", testBoard.getPassword());
        assertTrue(testBoard.validatePassword("123456"));
        assertFalse(testBoard.validatePassword("1"));
    }
}
