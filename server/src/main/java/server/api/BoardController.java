package server.api;

import commons.Board;

import commons.CardList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.BoardRepository;
import server.services.RandomService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository repo;

    private final RandomService randomService;

    /**
     * Constructs a new board controller
     * @param repo board repository
     * @param randomService service for generating random strings
     */
    public BoardController(BoardRepository repo, RandomService randomService){
        this.repo = repo;
        this.randomService = randomService;
    }

    /**
     * a web method which updates the color of the board in the database
     * @param id id of the board
     * @param color customized color
     * @return the board that was chosen
     */
    @PostMapping(path = "/update-color")
    public ResponseEntity<Board> editTitle(@RequestParam long id, @RequestParam String color) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.getById(id);
        board.setColor(color);
        board = repo.save(board);
        return ResponseEntity.ok(board);

    }


    /**
     * Endpoints for getting all boards in the database
     * @return list of boards in the database
     */

    @GetMapping(path = {"","/"})
    public List<Board> getAll(){
        return repo.findAll();
    }

    /**
     * Endpoint for getting a specific board in the database
     * @param id id of the board
     * @return board if it exists, empty optional otherwise
     */
    @GetMapping("/get-board")
    public Optional<Board> getBoard(@RequestParam long id){
        return repo.findById(id);
    }

    /**
     * Endpoint for getting a specific board in the database
     * @param key key of the board
     * @return board if it exists, empty optional otherwise
     */
    @GetMapping("/get-by-key")
    public Optional<Board> getBoard(@RequestParam String key){
        return repo
                .findAll()
                .stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst();
    }

    /**
     * Endpoint for adding a board to the database
     * @param board board to be added
     * @return board that is added to the database, or BAD_REQUEST status if board has no title
     */
    @PostMapping(path = {"", "/"})
    public ResponseEntity<Board> add(@RequestBody Board board){
        if (isNullOrEmpty(board.getTitle())) {
            return ResponseEntity.badRequest().build();
        }

        board.setKey(randomService.getRandomString());

        Board saved = repo.save(board);
        return ResponseEntity.ok(board);
    }

    /**
     * Endpoint for updating a board when a list is added to it
     * @param id id of the board
     * @param cardList cardlist to be added to the board
     * @return the board that was added to
     */
    @PostMapping(path = "/add-list")
    public ResponseEntity<Board> addList(@RequestParam long id, @RequestBody CardList cardList) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.getById(id);
        board.addCardList(cardList);
        board = repo.save(board);

        return ResponseEntity.ok(board);
    }


    /**
     * Endpoint for deleting a board from the database
     * @param id id of the board to delete
     * @return OK status if a board was removed, BAD_REQUEST status otherwise
     */
    @DeleteMapping(path = "/delete")
    public ResponseEntity<Board> removeBoard(@RequestParam long id){
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        repo.deleteById(id);

        return ResponseEntity.ok(null);
    }

    /**
     * Renames a board
     * @param title new title for the board
     * @param id id of the board to rename
     * @return OK status if a board was renamed, BAD_REQUEST status otherwise
     */
    @PostMapping(path = "/rename")
    public ResponseEntity<Board> renameBoard(@RequestParam String title, @RequestParam long id){
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.getById(id);
        board.setTitle(title);
        Board saved = repo.save(board);

        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint for updating a board's password
     *
     * @param id id of the board
     * @param password password of the board
     * @return the board that was edited
     */
    @PostMapping(path = "/edit-password")
    public ResponseEntity<Board> editDescription(@RequestParam long id,
                                                @RequestParam String password) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Board board = repo.getById(id);
        board.setPassword(password);
        repo.save(board);

        return ResponseEntity.ok(null);
    }

    /**
     * Checks whether a string is null or empty
     * @param s String to check
     * @return true if string is null or empty, false otherwise
     */
    private boolean isNullOrEmpty(String s){
        return s == null || s.equals("");
    }

}
