package se.claremont.backend.user.repository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;

import se.claremont.backend.user.repository.entities.User;

@Repository
public class UserRepositoryPostgreSQL implements UserRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private JpaEntityInformation<User, ?> entityInformation;

	@PostConstruct
	public void postConstruct() {
		this.entityInformation = JpaEntityInformationSupport.getEntityInformation(User.class, entityManager);
	}

	@Override
	@Transactional
	public <S extends User> S save(S entity) {
		if (entity.getId() == null) {
			entityManager.persist(entity);
		      return entity;
		    } else {
		      return entityManager.merge(entity);
		    }
	}

	@Override
	public User findByUsername(String username) {
		return entityManager
				.createQuery("SELECT u FROM User u WHERE u.username LIKE :username", User.class)
				.setParameter("username", username)
				.getSingleResult();
	}

	@Override
	public void deleteByUsername(String username) {
		entityManager.createQuery("DELETE FROM User u where u.username LIKE :username")
				.setParameter("username", username)
				.executeUpdate();
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

}
