package server.api;

import commons.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.TaskRepository;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository repo;

    /**
     * Creates a new task controller
     * @param repo repo with tasks
     */
    public TaskController(TaskRepository repo){
        this.repo = repo;
    }

    /**
     * Endpoint for adding tasks
     * @param task task to add to the database
     * @return OK status
     */
    @PostMapping("/add")
    public ResponseEntity<Task> addTask(@RequestBody Task task){
        repo.save(task);

        return ResponseEntity.ok(task);
    }

    /**
     * Endpoint for editing the status of a task
     * @param status new status of the task
     * @param id id of task to edit
     * @return OK status if task was edited, BAD_REQUEST status otherwise
     */
    @PostMapping("/edit-status")
    public ResponseEntity<Task> editStatus(@RequestParam boolean status, @RequestParam long id){
        if(id < 0 || !repo.existsById(id)){
            return ResponseEntity.badRequest().build();
        }

        Task task = repo.getById(id);

        task.setStatus(status);
        repo.save(task);

        return ResponseEntity.ok(null);
    }
}
