package se.claremont.backend.user.repository;

import org.springframework.data.repository.CrudRepository;

import se.claremont.backend.user.repository.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByUsername(String username);
	
	void deleteByUsername(String username);
	
}