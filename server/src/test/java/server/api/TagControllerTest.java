package server.api;

import commons.Board;
import commons.Card;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagControllerTest {
    private TagController sut;
    private TagTestRepository tagRepo;
    private CardRepositoryTest cardRepo;
    private BoardTestRepository boardRepo;
    private long boardId;
    private long cardId;
    private long tag1Id;

    @BeforeEach
    void setup(){
        tagRepo = new TagTestRepository();
        cardRepo = new CardRepositoryTest();
        boardRepo = new BoardTestRepository();

        Board board = new Board("My Board", null);
        Card card = new Card("My Card", null);

        Tag tag1 = new Tag("Tag1", "#FFFFFF");
        Tag tag2 = new Tag("Tag2", "#000000");

        board.addTag(tag1);
        board.addTag(tag2);

        card.addTag(tag1);
        card.addTag(tag2);

        boardRepo.save(board);
        cardRepo.save(card);
        tagRepo.save(tag1);
        tagRepo.save(tag2);

        boardId = board.getId();
        cardId = card.getId();
        tag1Id = tag1.getId();

        sut = new TagController(tagRepo, boardRepo, cardRepo);
    }


    @Test
    void getAllTest(){
        ArrayList<Tag> exp = new ArrayList<>(tagRepo.findAll());

        assertEquals(exp, sut.getAll(boardId));
    }

    @Test
    void getAllOfCardTest(){
        ArrayList<Tag> exp = new ArrayList<>(tagRepo.findAll());

        assertEquals(exp, sut.getAllOfCard(cardId));
    }

    @Test
    void editNameTest(){
        String nameExp = "new Name";

        sut.edit(tag1Id, nameExp);

        assertEquals(nameExp, tagRepo.getById(tag1Id).getName());
    }

    @Test
    void editColourTest(){
        String newColour = "#AAAAAA";

        sut.editColor(tag1Id, newColour);

        assertEquals(newColour, tagRepo.getById(tag1Id).getColor());
    }

    @Test
    void deleteTest(){
        sut.delete(tag1Id, boardId);

        assertFalse(tagRepo.existsById(tag1Id));
    }
}
