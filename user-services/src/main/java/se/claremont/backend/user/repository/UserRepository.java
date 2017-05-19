package se.claremont.backend.user.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import se.claremont.backend.user.repository.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

	public List<User> findByUsername(String username);
	
	public void deleteByUsername(String username);
	
}