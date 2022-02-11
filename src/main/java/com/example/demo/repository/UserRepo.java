package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.User;
import com.example.demo.repository.query.UserRepoQuery;

public interface UserRepo extends JpaRepository<User, Long>, UserRepoQuery{

	
}
