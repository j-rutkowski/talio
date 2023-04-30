package server.api;

import commons.Card;
import commons.Tag;
import commons.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CardRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardRepository repo;

    /**
     * Constructs a new card controller
     *
     * @param repo cards repository
     */
    public CardController(CardRepository repo) {
        this.repo = repo;
    }

    /**
     * Endpoint for retrieving all the cards currently in the database
     *
     * @return a list with all the cards that are in the database
     */
    @GetMapping(path = { "", "/" })
    public List<Card> getAll() {
        return repo.findAll();
    }

    /**
     * Endpoint for retrieving one specific card currently in the database
     *
     * @param id id of the card
     * @return a list with all the cards that are in the database
     */
    @PostMapping(path = { "/getCard" })
    public Optional<Card> getCard(@RequestParam long id) {
        return repo.findById(id);
    }

    /**
     * Endpoint for adding a card to the database
     *
     * @param card  The card to be added to the database
     * @return      The card that is added to the database
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Card> add(@RequestBody Card card) {

        if (isNullOrEmpty(card.getTitle())) {
            return ResponseEntity.badRequest().build();
        }

        Card saved = repo.save(card);
        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint for deleting a card
     *
     * @param id id of card
     * @return deleted card
     */
    @DeleteMapping(path = { "", "/" })
    public Optional<Card> removeCard(@RequestParam Long id) {
        Optional<Card> card = repo.findById(id);
        System.out.println("TITLE" + card.get().getTitle());
        repo.deleteById(id);
        return card;
    }

    /**
     * Edit the card
     *
     * @param id id of the card to be edited
     * @param title new title
     * @return the edited card
     */
    @PostMapping(path = "/edit")
    public ResponseEntity<Card> editTitle(@RequestParam long id, @RequestParam String title) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Card card = repo.getById(id);
        card.setTitle(title);
        card = repo.save(card);

        return ResponseEntity.ok(card);
    }

    /**
     * Edits the description of a card
     * @param id id of the card
     * @param description new description for the card
     * @return OK status if a card was edited, BAD_REQUEST otherwise
     */
    @PostMapping(path = "/edit-desc")
    public ResponseEntity<Card> editDescription(@RequestParam long id,
                                                @RequestParam String description) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Card card = repo.getById(id);
        card.setDescription(description);
        repo.save(card);

        return ResponseEntity.ok(null);
    }

    /**
     * A method that edits the list of tasks on the server,
     * you cannot just do setTasks, as that does not work
     * @param id the id of the card
     * @param tasks the new tasklist
     * @return OK status if a card was edited, BAD_REQUEST otherwise
     */
    @PostMapping(path = "/edit-task")
    public ResponseEntity<Card> editTasks(@RequestParam long id,
                                          @RequestBody List<Task> tasks) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Card card = repo.getById(id);
        List<Task> list = card.getTasks();

        if(list.size() > tasks.size()){
            removeTasks(card, tasks, list);
        }
        else{
            for (int i = 0; i < tasks.size(); i++) {
                try{
                    if(!list.get(i).equals(tasks.get(i))){
                        card.updateTask(tasks.get(i), i);
                    }
                }
                catch (IndexOutOfBoundsException e){
                    card.addTask(tasks.get(i));
                }
            }
        }

        repo.save(card);

        return ResponseEntity.ok(null);
    }

    /**
     * A method that edits the list of tags on the server,
     * @param id the id of the card
     * @param tags the new tagslist
     * @return OK status if a card was edited, BAD_REQUEST otherwise
     */
    @PostMapping(path = "/edit-tags")
    public ResponseEntity<Card> editTags(@RequestParam long id,
                                          @RequestBody List<Tag> tags) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Card card = repo.getById(id);



        List<Tag> list = card.getTags();

        if(list.size() > tags.size()){
            removeTags(card, tags, list);
        }
        else{
            for (int i = 0; i < tags.size(); i++) {
                try{
                    if(!list.get(i).equals(tags.get(i))){
                        card.updateTag(tags.get(i), i);
                    }
                }
                catch (IndexOutOfBoundsException e){
                    card.addTag(tags.get(i));
                }
            }
        }

        repo.save(card);

        return ResponseEntity.ok(null);
    }

    /**
     * A method that deals with if there are cards removed at the card
     * @param card the card
     * @param tasks the new list
     * @param list the old list
     */
    public void removeTasks(Card card, List<Task> tasks, List<Task> list){

        for (int i = 0; i < list.size(); i++) {
            boolean contains = false;
            int index = 0;
            Task t = list.get(i);
            for (int j = 0; j < tasks.size(); j++) {
                Task task = tasks.get(j);
                if (t.getId() == task.getId()) {
                    contains = true;
                    index = j;
                    break;
                }
            }
            if(!contains){
                list.remove(i);
                i--;
            }
            else{
                card.updateTask(tasks.get(index), index);
            }
        }
        card.setTasks(list);
    }

    /**
     * A method that deals with if there are cards removed at the card
     * @param card the card
     * @param tasks the new list
     * @param list the old list
     */
    public void removeTags(Card card, List<Tag> tasks, List<Tag> list){

        for (int i = 0; i < list.size(); i++) {
            boolean contains = false;
            int index = 0;
            Tag t = list.get(i);
            for (int j = 0; j < tasks.size(); j++) {
                Tag task = tasks.get(j);
                if (t.getId() == task.getId()) {
                    contains = true;
                    index = j;
                    break;
                }
            }
            if(!contains){
                list.remove(i);
                i--;
            }
            else{
                card.updateTag(tasks.get(index), index);
            }
        }
        card.setTags(list);
    }

    /**
     * Function for removing the cards from a list
     *
     * @param cards The cards to be removed
     */
    public void removeCards(List<Card> cards) {
        System.out.println("Removing cards");
        for(Card card : cards) {
            this.repo.delete(card);
        }
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
