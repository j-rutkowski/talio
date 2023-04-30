package server.api;

import commons.Card;
import commons.Tag;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import server.Main;
import server.api.CardController;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardControllerTest {

    private CardController sut;
    private Card testCard;

    @BeforeEach
    public void setup(){
        sut = new CardController(new CardRepositoryTest());
        testCard = new Card("title", "desc");
    }

    @Test
    public void getAllTest(){
        Card card1 = new Card("title",null);
        Card card2 = new Card("title",null);

        Card[] list = {card1, card2};

        sut.add(card1);
        sut.add(card2);

        List<Card> res = sut.getAll();

        assertArrayEquals(list, res.toArray());
    }

    @Test
    public void editTitleTest(){

        sut.add(testCard);

        String expected = "new title";
        sut.editTitle(testCard.getId(), expected);

        String res = sut.getCard(testCard.getId()).get().getTitle();

        assertEquals(expected, res);
    }

    @Test
    public void editDescription(){
        String expected = "new desc";

        sut.add(testCard);
        sut.editDescription(testCard.getId(), expected);

        String res = sut.getCard(testCard.getId()).get().getDescription();

        assertEquals(expected, res);
    }

    @Test
    public void editTasksTest(){
        Task[] exp = {new Task("newTask1"), new Task("newTask2")};

        sut.add(testCard);

        sut.editTasks(testCard.getId(), List.of(exp));

        List<Task> res = sut.getCard(testCard.getId()).get().getTasks();

        assertEquals(List.of(exp), res);
    }

    @Test
    public void editTagsTest(){
        Tag[] arr = {new Tag("tag1", null), new Tag("tag2", null)};
        List<Tag> exp = List.of(arr);
        testCard.setTags(new ArrayList<>());
        sut.add(testCard);
        sut.editTags(testCard.getId(), exp);

        List<Tag> res = sut.getCard(testCard.getId()).get().getTags();

        assertEquals(exp, res);
    }

    @Test
    public void removeTasksTest(){
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Task 1"));
        tasks.add(new Task("Task 2"));

        testCard.setTasks(tasks);
        tasks.remove(1);

        sut.removeTasks(testCard, tasks, testCard.getTasks());
        assertEquals(1, testCard.getTasks().size());
        sut.removeTasks(testCard, new ArrayList<>(), testCard.getTasks());
        assertEquals(0, testCard.getTasks().size());

    }

    @Test
    public void removeTagsTest(){
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Tag 1", "color"));
        tags.add(new Tag("Tag 2", "color2"));

        testCard.setTags(tags);
        tags.remove(1);

        assertEquals(1, tags.size());
        sut.removeTags(testCard, tags, testCard.getTags());
        //assertEquals(1, testCard.getTags().size());
        sut.removeTags(testCard, new ArrayList<>(), testCard.getTags());
        assertEquals(0, testCard.getTags().size());
        List<Card> cards = new ArrayList<>();
        cards.add(testCard);
        sut.removeCards(cards);
    }

}
