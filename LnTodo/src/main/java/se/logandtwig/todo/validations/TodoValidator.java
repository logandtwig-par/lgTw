package se.logandtwig.todo.validations;

import se.logandtwig.todo.controller.response.TodoDto;

public class TodoValidator {

	public void validate(TodoDto todo) {
		if (todo == null) throw new IllegalStateException("Todo should not be null");
		if (todo.getTask() == null || todo.getTask().isEmpty()) throw new IllegalStateException("Task should not be empty");
	}
}
