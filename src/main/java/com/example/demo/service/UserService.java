package com.example.demo.service;

import java.util.List;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;

public interface UserService {

	User saveUser(User user);

	Role saveRole(Role role);
	
	Permission savePermission(Permission permission);

	void addRoleToUser(String username, String roleName);

	User getUser(String username);
	
	User getUserById(Long id);
	
	Role getRole(Long id);

	Permission getPermission(Long id);

	List<User> getUsers();
	
	List<Role> getRoles();

	List<Permission> getPermissions();
}
