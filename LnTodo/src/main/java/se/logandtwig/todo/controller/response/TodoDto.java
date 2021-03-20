package se.logandtwig.todo.controller.response;

/**
 * This is the response representation of a single task.
 */
public class TodoDto {

	public TodoDto() {
	}

	public TodoDto(Long id, String task, String username) {
		this.id = id;
		this.task = task;
		this.username = username;
	}

	private Long id;
	private String task;
	private String username;

	/**
	 * @return The unique ID of the task
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return The actual task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @return The owner of the task
	 */
	public String getUsername() {
		return username;
	}
}
