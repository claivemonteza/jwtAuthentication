package com.example.demo.repository.query;

import com.example.demo.domain.User;

public interface UserRepoQuery {

	User findByUsername(String username);
}
