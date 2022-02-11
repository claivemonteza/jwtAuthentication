package com.example.demo.repository.query.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.example.demo.domain.User;
import com.example.demo.repository.query.UserRepoQuery;

public class UserRepoImpl implements UserRepoQuery {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public User findByUsername(String username) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);

		Root<User> root = criteria.from(User.class);

		criteria.select(root);

		criteria.where(builder.equal(root.get("username"), username));

		TypedQuery<User> query = entityManager.createQuery(criteria);
		return query.getSingleResult();
	}

}
