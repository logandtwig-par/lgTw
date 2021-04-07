package se.logandtwig.todo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<TodoEntity> todoEntities = new ArrayList<>();

    public UserEntity(String username) {
        this.username = username;
    }

    public UserEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<TodoEntity> getTodoEntities() {
        return todoEntities;
    }

    public void setTodoEntities(List<TodoEntity> todoEntities) {
        this.todoEntities = todoEntities;
    }
}
