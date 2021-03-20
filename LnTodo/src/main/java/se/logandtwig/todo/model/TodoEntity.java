package se.logandtwig.todo.model;

import javax.persistence.*;

@Entity(name = "todos")
public class TodoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; // was int

	/**
	 * The title of the task
	 */
	private String task;

	/**
	 * The title of the task
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private UserEntity owner;


	public Long getId() {
		return id;
	}

	public String getTask() {
		return task;
	}

	public UserEntity getOwner() {
		return owner;
	}
}