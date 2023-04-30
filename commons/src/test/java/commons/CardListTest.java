package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardListTest {

    @Test
    public void testConstructor() {
        CardList list = new CardList();
        assertNotNull(list);
    }

    @Test
    public void testGetId() {
        CardList list = new CardList("List 1", new ArrayList<>());
        assertEquals(0L, list.getId());
    }

    @Test
    public void testSetTitle() {
        CardList list = new CardList("List 1", new ArrayList<>());
        list.setTitle("New Title");
        assertEquals("New Title", list.getTitle());
    }

    @Test
    public void testAddCard() {
        CardList list = new CardList("List 1", new ArrayList<>());
        Card card = new Card("Card 1", "Description 1");
        list.addCard(card);
        assertTrue(list.getCards().contains(card));
    }

    @Test
    public void testInsertCard() {
        CardList list = new CardList("List 1", new ArrayList<>());
        Card card1 = new Card("Card 1", "Description 1");
        Card card2 = new Card("Card 2", "Description 2");
        Card card3 = new Card("Card 3", "Description 3");
        list.addCard(card1);
        list.addCard(card2);
        list.insertCard(card3, 1);
        assertEquals(card3, list.getCards().get(1));
    }

    @Test
    public void testSetCards() {
        CardList list = new CardList("List 1", new ArrayList<>());
        List<Card> cards = new ArrayList<>();
        cards.add(new Card("Card 1", "Description 1"));
        cards.add(new Card("Card 2", "Description 2"));
        list.setCards(cards);
        assertEquals(cards, list.getCards());
    }

    @Test
    public void testEquals() {
        CardList list1 = new CardList("List 1", new ArrayList<>());
        CardList list2 = new CardList("List 1", new ArrayList<>());
        assertEquals(list1, list2);
    }

    @Test
    public void testHashCode() {
        CardList list1 = new CardList("List 1", new ArrayList<>());
        CardList list2 = new CardList("List 1", new ArrayList<>());
        assertEquals(list1.hashCode(), list2.hashCode());
    }

    @Test
    public void testToString() {
        CardList list = new CardList("List 1", new ArrayList<>());
        String expected = "CardList{id=0, title='List 1', cards=[]}";
        assertEquals(expected, list.toString());
    }
}