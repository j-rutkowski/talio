package server.api;

import commons.Board;
import commons.Card;
import commons.CardList;
import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.TagRepository;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagRepository repo;

    private final BoardRepository boardRepository;

    private final CardRepository cardRepository;

    /**
     * Constructs a new card controller
     *
     * @param tagRepository tag repository
     * @param boardRepository repository of boards
     * @param cardRepository repository of cards
     */
    public TagController(TagRepository tagRepository,
                         BoardRepository boardRepository,
                         CardRepository cardRepository) {
        this.repo = tagRepository;
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Endpoint for retrieving all the tags currently in the database
     *
     * @param boardId id of the board to add the tag to
     * @return a list with all the tags that are in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Tag> getAll(@RequestParam Long boardId) {
        return boardRepository.getById(boardId).getTags();
    }

    /**
     * Endpoint for retrieving all the tags currently in the database
     *
     * @param cardId id of the card to get the tags from
     * @return a list with all the tags that are in the database
     */
    @GetMapping(path = { "/card" })
    public List<Tag> getAllOfCard(@RequestParam Long cardId) {
        Card card = cardRepository.getById(cardId);
        return card.getTags();
    }

    /**
     * Endpoint for adding a tag to the database
     *
     * @param tag  The tag to be added to the database
     * @return      The tag that is added to the database
     * @param boardId id of the board to add the tag to
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Tag> add(@RequestBody Tag tag, @RequestParam Long boardId) {

        if (isNullOrEmpty(tag.getName()) || isNullOrEmpty(tag.getColor())) {
            return ResponseEntity.badRequest().build();
        }

        Board board = boardRepository.getById(boardId);

        Board b = board.addTag(tag);
        boardRepository.save(b);
        return ResponseEntity.ok(tag);

    }

    /**
     * Endpoint for editing a tag to the database
     *
     * @param tagId  The id of the tag to be edited
     * @param newName new name of the tag
     */
    @GetMapping( "/edit" )
    public void edit(@RequestParam Long tagId, @RequestParam String newName) {
        Tag t = this.repo.getById(tagId);
        System.out.println(t.getId() + " " + t.getName());
        t.setName(newName);
        this.repo.save(t);
    }

    /**
     * Endpoint for editing the color of a tag
     *
     * @param tagId  The id of the tag to be edited
     * @param newColor new color of the tag
     */
    @GetMapping( "/editColor" )
    public void editColor(@RequestParam Long tagId, @RequestParam String newColor) {
        Tag t = this.repo.getById(tagId);
        t.setColor(newColor);
        this.repo.save(t);
        return;
    }




    /**
     * Endpoint for deleteing a tag
     *
     * @param tagId  The id of the tag to be edited
     * @param boardId id of the board of the tag
     */
    @DeleteMapping( "/delete" )
    public void delete(@RequestParam Long tagId, @RequestParam Long boardId) {
        System.out.println("DELETING");

        Board b = this.boardRepository.getById(boardId);
        Tag t = this.repo.getById(tagId);
        System.out.println("TAG = " + t.getId() + " - " + t.getName());
        System.out.println("BOARD = " + b.getId() + " - " + b.getTitle());

        for(CardList cl : b.getCardLists()) {
            for(Card c : cl.getCards()) {
                c.getTags().remove(t);
            }
        }

        b.getTags().remove(t);
        this.boardRepository.save(b);

        this.repo.delete(t);

        return;
    }



    /**
     * Function to check whether a string is null or empty
     *
     * @param s     String to be checked if it is empty
     * @return      True, when s is null or empty, false otherwise
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
