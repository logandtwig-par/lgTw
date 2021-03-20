package se.logandtwig.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.logandtwig.todo.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
