package se.logandtwig.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import se.logandtwig.todo.controller.response.TodoDto;
import se.logandtwig.todo.model.TodoEntity;
import se.logandtwig.todo.model.UserEntity;
import se.logandtwig.todo.repository.TodoRepository;
import se.logandtwig.todo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TodoApi {

	@Autowired
	private TodoRepository todoRepository;
	private UserRepository userRepository;

/*Home message*/
    @GetMapping("/")
    public String home(){
        return "SAY HIIII!!";
    }

/*List all players in the player database* /
    @GetMapping(path="/player")
    @CrossOrigin()

    List<Player> getAll(){
		
		var l = new ArrayList<Player>();
		for(Player r : playerRepository.findAll())
		{
			l.add(r);
		}
		return l;
	}

/** * /
2. En GET-endpoint som svarar med en lista som innehåller en given användares samtliga TODOs i databasen.
/* * */
	@GetMapping("/todo")
	public List<TodoEntity> getAll(@RequestParam(value = "username") String username) {
		
		ArrayList<TodoEntity> todoList = new ArrayList<TodoEntity>();
/** * /
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

		return todoList;//new ArrayList<>(); // Implement me
	}

/** /
3. En GET-endpoint som svarar med en specifik TODO i databasen givet ett ID, förutsatt att den tillhör användaren.
    3.1 Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt.
/**/
	@GetMapping("/todo/{id}")
	public TodoDto getOne(@PathVariable(value = "id") Long id, //ToDo ID
	                      @RequestParam(value = "username") String username) { //The user --> Make sure the user owns this ToDo ID
		String Return_String;

		if (!todoRepository.findById(id).isPresent()) {//Check if the ToDo ID exists
			Return_String="ID DOES NOT EXIST DUDE";
		} else {

			if (todoRepository.findById(id).get().getOwner().getUsername().equals(username)) { //getOwner is a UserEntity. Use to match with the findById in the TodoEntity
				Return_String = todoRepository.findById(id).get().getTask();
			} else {
				Return_String="THE DUDE DOES NOT MATCH THE ID, DUDE";
			}

		}	

		return new TodoDto(id,Return_String,username);
	}


/** * /
1. En POST-endpoint som sparar en ny TODO till databasen och svarar med den skapade TODOn, inkl. IDt.
/** * /

	@PostMapping("/todo")
	public TodoDto create(@RequestBody TodoDto todo,
	                      @RequestParam(value = "username") String username) {

		//return new TodoDto(); // Implement me
	}

/** */

//--> Bytte till TodoEntity, fattade inte hur jag skulle fån värdena från TodoDto utan setters
@PostMapping("/todo")
public TodoDto create(@RequestBody TodoEntity todo, 
					  @RequestParam(value = "username") String username) {

	todoRepository.save(todo);
	String taskInsertInEntity = todo.getTask();


	return new TodoDto(todo.getId(),taskInsertInEntity,"THIS IS NOT RETURNED");
}

/** * / 
4. En DELETE-endpoint som tar bort en specifik TODO från databasen, förutsatt att den tillhör användaren.
    4.1 Om det givna IDt för TODOn inte matchar den givna användarens username ska detta hanteras på lämpligt sätt.
/** */

	@DeleteMapping("/todo/{id}")
	public TodoDto delete(@PathVariable(value = "id") Long id,
	                      @RequestParam(value = "username") String username) {
		todoRepository.deleteById(id);
		return new TodoDto(); // Implement me
	}

	/*Change a player with the selected id with PUT --> all info needs to be sent again* /
@PutMapping(path="/player/{id}", consumes="application/json", produces="application/json")
@CrossOrigin()
Player update(@PathVariable Integer id, @RequestBody Player updatedPlayer){ //get player data from the rest interface
	Player dbPlayer = playerRepository.findById(id).get(); //get player by id from the database

	dbPlayer.setBorn(updatedPlayer.getBorn());
	dbPlayer.setAge(updatedPlayer.getAge());
	dbPlayer.setName(updatedPlayer.getName());
	dbPlayer.setJersey(updatedPlayer.getJersey());

	playerRepository.save(dbPlayer);

	return dbPlayer;
}

/**/

	// Optional bonus! //
	@PutMapping("/todo")
	public TodoDto edit(@RequestBody TodoDto todo,
	                    @RequestParam(value = "username") String username) {
		return new TodoDto(); // Implement me
	}
}
