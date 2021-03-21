package se.logandtwig.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import se.logandtwig.todo.controller.response.TodoDto;
import se.logandtwig.todo.model.TodoEntity;
import se.logandtwig.todo.model.UserEntity;
import se.logandtwig.todo.repository.TodoRepository;
import se.logandtwig.todo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TodoApi {

	@Autowired private UserRepository userRepository;
	@Autowired private TodoRepository todoRepository;
	

/*Home message*/
    @GetMapping("/")
    public String home(){
        return "SAY HIIII!!";
    }


/** * /
2. En GET-endpoint som svarar med en lista som innehåller en given användares samtliga TODOs i databasen.
/* * */
	@GetMapping("/todo")
	public ResponseEntity<List<TodoEntity>> getAll(@RequestParam(value = "username") String username) {
		
		ArrayList<TodoEntity> todoList = new ArrayList<TodoEntity>();

/** Didn't figure extended for out....outherwise that looks way more badass * /
		for(TodoEntity r : todoRepository.findAll())
		{
			todoList.add(r);
		}
/** */

		for (int i = 0; i < todoRepository.count(); i++) {

			if (todoRepository.findAll().get(i).getOwner().getUsername().equals(username)){
				todoList.add(todoRepository.findAll().get(i));
			}
		}

		if (todoList.isEmpty()) { //nothing found for this user
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(todoList);//new ArrayList<>(); // Implement me
	}

/** /
3. En GET-endpoint som svarar med en specifik TODO i databasen givet ett ID, förutsatt att den tillhör användaren.
    3.1 Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt.
/**/
	@GetMapping("/todo/{id}")
//	public TodoDto getOne(@PathVariable(value = "id") Long id, @RequestParam(value = "username") String username) { //ToDo ID //The user --> Make sure the user owns this ToDo ID
	public ResponseEntity<TodoDto> getOne (@PathVariable(value = "id") Long id, @RequestParam(value = "username") String username) { //ToDo ID //The user --> Make sure the user owns this ToDo ID	

		try { //just in case someone throws null into id
			if (!todoRepository.findById(id).isPresent() || !todoRepository.findById(id).get().getOwner().getUsername().equals(username)) {//Check if the ToDo ID and username exists
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(new TodoDto(id,todoRepository.findById(id).get().getTask(),username));
				
/*alternative returns* /
		TodoDto p = new TodoDto();
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(p.getId())
        .toUri();
		
		return ResponseEntity.created(location).header("MyResponseHeader", "MyValue").contentType(MediaType.APPLICATION_JSON).body(new TodoDto(id,Return_String,username));
	/**/
		
		//return new TodoDto(id,Return_String,username);
	}


/** * /
1. En POST-endpoint som sparar en ny TODO till databasen och svarar med den skapade TODOn, inkl. IDt.
/** */
//--> Bytte till TodoEntity i requestbodyn, fattade inte hur jag skulle få värdena från TodoDto utan setters i entityn
	@PostMapping("/todo")
	public ResponseEntity<TodoDto> create(@RequestBody TodoEntity todo, @RequestParam(value = "username") String username) {
		
		//Check if the ToDo ID and username exists
		if ((todo.getTask().isEmpty()) || (todo.getOwner() == null) || (todo.getOwner().getId() == null)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "id(owner:{id:}) or task description(task:) is missing").build();
		}

		//Check if username matches userID in the request. If it does, save and return, otherwise NOT_FOUND reply 
		for (int i = 0; i < userRepository.count(); i++) { 
			if ((userRepository.findAll().get(i).getUsername().equals(username)) && (todo.getOwner().getId() == userRepository.findAll().get(i).getId())) {
				todoRepository.save(todo);
				return ResponseEntity.ok(new TodoDto(todo.getId(),todo.getTask(),username));
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "id did not match with the username").build();
		
	}

/** * / 
4. En DELETE-endpoint som tar bort en specifik TODO från databasen, förutsatt att den tillhör användaren.
    4.1 Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt.
/** */

	@DeleteMapping("/todo/{id}")
	public ResponseEntity<TodoDto> delete(@PathVariable(value = "id") Long id,@RequestParam(value = "username") String username) {

		try {
			if (!todoRepository.findById(id).get().getOwner().getUsername().equals(username)){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "Task id does not match username").build();	
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "Task id is not defined").build();	
		}
		
		todoRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.OK).header("Message", "Item was deleted").build();		
		
	}

/**/

	// Optional bonus! //
	@PutMapping("/todo")
	public ResponseEntity<TodoDto> edit(@RequestBody TodoEntity todo,@RequestParam(value = "username") String username) {
		
		//Check input values, entire todo entity needed
		if (todo.getId().equals(null) || 
			todo.getTask().isEmpty() || 
			todo.getOwner().equals(null) ||
			todo.getOwner().getUsername().isEmpty() || 
			todo.getOwner().getId().equals(null)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "Fill in all fields in the todo entity").build();
		}

		try {
			if (!todoRepository.findById(todo.getId()).get().getOwner().getUsername().equals(username)){
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "Task id does not match username").build();	
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error Message", "Task id is not defined").build();	
		}
		
		todoRepository.save(todo);
		return ResponseEntity.status(HttpStatus.OK).header("Message", "Item was changed").body(new TodoDto(todo.getId(),todo.getTask(),username));		
	
	}
}
