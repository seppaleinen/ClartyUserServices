package se.claremont.backend.user.repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;

import se.claremont.backend.user.repository.entities.User;


@Repository
public class UserRepositoryMock implements UserRepository{

	private static List<User> users = new ArrayList<>();
	
	static {
		User user = new User();
		user.setId(1);
		user.setUsername("axel.jansson@claremont.se");
		users.add(user);
	}

	@Override
	public <S extends User> S save(S entity) {
		entity.setId(users.size() + 1);
		users.add(entity);
		return entity;
	}

	@Override
	public <S extends User> Iterable<S> save(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<User> findAll(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(User entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends User> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<User> findByUsername(String username) {
		List<User> usersTemp = null;
		for (User user : users) {
			if(user.getUsername().equals(username)){
				usersTemp = new ArrayList<>();
				usersTemp.add(user);
				return usersTemp;
			}
		}
		return usersTemp;
	}

	@Override
	public void deleteByUsername(String username) {
		// TODO Auto-generated method stub
		
	};
	


}
