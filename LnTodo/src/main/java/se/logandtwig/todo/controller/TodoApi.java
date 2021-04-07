package se.logandtwig.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.logandtwig.todo.controller.response.TodoDto;
import se.logandtwig.todo.model.TodoEntity;
import se.logandtwig.todo.model.UserEntity;
import se.logandtwig.todo.repository.TodoRepository;
import se.logandtwig.todo.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class TodoApi {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TodoRepository todoRepository;

    /* Home message */
    @GetMapping("/")
    @CrossOrigin
    public String home() {
        return "SAY HIIII!!";
    }

    @GetMapping("/todo")
    @CrossOrigin
    public ResponseEntity<List<TodoDto>> getAll(@RequestParam(value = "username") String username) {

        // ignoring concerns about no splitting api, domain and database code you could simply map the list and return it, once a proper repository method is declared.
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            List<TodoDto> todoEntities = user.get().getTodoEntities()
                    .stream()
                    .map(todoEntity -> new TodoDto(todoEntity.getId(), todoEntity.getTask(), todoEntity.getOwner().getUsername()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(todoEntities, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
    }

    @GetMapping("/todo/{id}")
    @CrossOrigin
    public ResponseEntity<TodoDto> getOne(@PathVariable(value = "id") Long id,
            @RequestParam(value = "username") String username) {

        // Basically what you did, but more readable
        Optional<TodoEntity> todo = todoRepository.findById(id);
        if (todo.isPresent()) {
            if (username.equals(todo.get().getOwner().getUsername())) {
                TodoDto todoDto = new TodoDto(todo.get().getId(), todo.get().getTask(), todo.get().getOwner().getUsername());
                return new ResponseEntity<>(todoDto, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * * / 1. En POST-endpoint som sparar en ny todo till databasen och svarar med den skapade TODOn, inkl. IDt. /**
     */
    @PostMapping("/todo")
    @CrossOrigin
    public ResponseEntity<TodoDto> create(@RequestBody TodoDto todo, // Use Dto instead
            @RequestParam(value = "username") String username) {
        // Id should be generated serverside and ignored on creation. POST should be idempotent
        if ((todo.getTask().isEmpty()) || todo.getUsername() == null || todo.getUsername().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Error Message", "id(owner:{id:}) or task description(task:) is missing")
                    .build();
        }

        // create entity from request
        UserEntity owner = userRepository.findByUsername(todo.getUsername()).orElse(new UserEntity(username));
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTask(todo.getTask());
        todoEntity.setOwner(owner);
        // persist new entity
        TodoEntity entity = todoRepository.save(todoEntity);
        // construct new dto for response
        TodoDto result = new TodoDto(entity.getId(), entity.getTask(), entity.getOwner().getUsername());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * * / 4. En DELETE-endpoint som tar bort en specifik todo från databasen, förutsatt att den tillhör användaren. 4.1
     * Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt. /**
     */

    @DeleteMapping("/todo/{id}")
    @CrossOrigin
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id,
            @RequestParam(value = "username") String username) {

        Optional<TodoEntity> todo = todoRepository.findById(id);
        if (todo.isPresent() && username != null && !username.trim().isEmpty()) {
            if (username.equals(todo.get().getOwner().getUsername())) {
                todoRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**/
    // Sorry, ran out of time...
    // Optional bonus! //
    @PutMapping("/todo")
    @CrossOrigin
    public ResponseEntity<TodoDto> edit(@RequestBody TodoEntity todo,
            @RequestParam(value = "username") String username) {

        // Check input values, entire todo entity needed
        if (todo.getId().equals(null) ||
                todo.getTask().isEmpty() ||
                todo.getOwner().equals(null) ||
                todo.getOwner().getUsername().isEmpty() ||
                todo.getOwner().getId().equals(null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Error Message", "Fill in all fields in the todo entity")
                    .build();
        }

        try {
            if (!todoRepository.findById(todo.getId()).get().getOwner().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("Error Message", "Task id does not match username")
                        .build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Error Message", "Task id is not defined")
                    .build();
        }

        todoRepository.save(todo);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Message", "Item was changed")
                .body(new TodoDto(todo.getId(), todo.getTask(), username));

    }
}
