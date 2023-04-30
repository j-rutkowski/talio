package server.api;

import commons.Board;
import commons.Card;
import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.database.CardRepository;
import server.database.ListRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lists")
public class ListController {

    private final ListRepository repo;

    private final CardController cardController;

    private final CardRepository cardRepo;

    private final BoardRepository boardRepo;


    /**
     * Constructs a new card controller
     *
     * @param repo cards repository
     * @param cardController controller of cards
     * @param cardRepository repository of cards
     * @param boardRepo repository of boards
     */
    public ListController(ListRepository repo, CardController cardController,
                          CardRepository cardRepository, BoardRepository boardRepo) {
        this.repo = repo;
        this.cardController = cardController;
        this.cardRepo = cardRepository;
        this.boardRepo = boardRepo;
    }

    /**
     * Endpoint for retrieving all the cards currently in the database
     *
     * @return a list with all the cards that are in the database
     */
    @GetMapping(path = { "", "/" })
    public List<CardList> getAll() {
        return repo.findAll();
    }

    /**
     * Endpoint for retrieving a specific list currently in the database
     *
     * @param id id of the list
     * @return a list with all the cards that are in the database
     */
    @PostMapping(path = { "getList" })
    public Optional<CardList> getList(Long id) {
        return repo.findById(id);
    }

    /**
     * Endpoint for adding a card to the database
     *
     * @param cardList  The card to be added to the database
     * @return      The card that is added to the database
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<CardList> add(@RequestBody CardList cardList) {

        if (isNullOrEmpty(cardList.getTitle())) {
            return ResponseEntity.badRequest().build();
        }

        CardList saved = repo.save(cardList);
        return ResponseEntity.ok(saved);
    }

    /**
     * Edit the title of the list
     *
     * @param id id of the list to be edited
     * @param title new title
     * @return the edited list
     */
    @PostMapping(path = "/editName")
    public ResponseEntity<CardList> editName(@RequestParam long id, @RequestParam String title) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        CardList cardList = repo.getById(id);
        cardList.setTitle(title);
        cardList = repo.save(cardList);
        return ResponseEntity.ok(cardList);
    }
    /**
     * Edit the title of the list
     *
     * @param id id of the list to be reordered
     * @param index the index to be inserted
     * @param card  the card to be inserted
     * @return the edited list
     */
    @PostMapping(path = "/reorderList")
    public ResponseEntity<CardList> editName(@RequestParam long id,
                                             @RequestParam int index, @RequestBody Card card) {
        CardList cardList = repo.getById(id);
        cardList.getCards().add(index,card);
        cardList = repo.save(cardList);
        return ResponseEntity.ok(cardList);
    }


    /**
     * Endpoint for removing a card
     *
     * @param cardId id of card to be removed
     * @return  removed card
     */
    @DeleteMapping(path = { "/card" })
    public Card removeCard(@RequestParam Long cardId) {
        System.out.println("REMOVING CARD = " + cardId);
        try {
            List<CardList> cardLists = repo.findAll();
            for (CardList cardList : cardLists) {
                for (Card c : cardList.getCards()) {
                    if (c.getId() == cardId) {
                        System.out.println("Found card to remove = " + c.toString());
                        cardList.getCards().remove(c);
                        repo.save(cardList);
                        return new Card("", "");
                    }
                }
            }
        } catch (Error e) {
            System.out.println("Error: " + e);
        }
        return new Card("", "");
    }

    /**
     * Endpoint for removing a list
     *
     * @param listId id of list to be removed
     * @param boardId id of board
     * @return  removed list
     */
    @DeleteMapping(path = { "/list" })
    public CardList removeList(@RequestParam Long listId, @RequestParam Long boardId) {
        System.out.println("Received list remove");

        Board board = boardRepo.findById(boardId).get();
        System.out.println("Board = " + board.toString());

        CardList cardList = repo.getById(listId);
        System.out.println("Cardlist = " + cardList.toString());

        board.getCardLists().remove(cardList);
        boardRepo.save(board);
        return cardList;
    }

    /**
     * A method that edits/updated the list by adding a card to the list
     * @param listId the id of the list
     * @param card the card you add
     * @return the new list
     */
    @PostMapping(path =  { "edit", "/edit" })
    public ResponseEntity<CardList> edit(@RequestParam long listId, @RequestBody Card card) {
        System.out.println("editing Card " + card.toString());
        if (listId < 0 || !repo.existsById(listId)) {
            return ResponseEntity.badRequest().build();
        }
        CardList cardList = repo.getById(listId);
        cardList.addCard(card);
        repo.save(cardList);
        return ResponseEntity.ok(null);
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
