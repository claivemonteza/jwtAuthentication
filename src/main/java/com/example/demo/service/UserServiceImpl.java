package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.PermissionRepo;
import com.example.demo.repository.RoleRepo;
import com.example.demo.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepo userRepo;

	private final RoleRepo roleRepo;
	
	private final PermissionRepo permissionRepo;

	private final PasswordEncoder passwordEncoder;

	@Override
	public User saveUser(User user) {
		log.info("saving new user {} to the database", user.getName());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		log.info("Saving new role {} to the database", role.getName());
		return roleRepo.save(role);
	}
	
	@Override
	public Permission savePermission(Permission permission) {
		log.info("Saving new permission {}", permission.getDescription());
		return permissionRepo.save(permission);
	}

	@Override
	public void addRoleToUser(String username, String roleName) {

		log.info("Adding role {} to user {}", roleName, username);
		User user = userRepo.findByUsername(username);
		Role role = roleRepo.findByName(roleName);
		user.getRoles().add(role);
	}

	@Override
	public User getUser(String username) {
		log.info("Fetching user {}", username);
		return userRepo.findByUsername(username);
	}
	
	@Override
	public User getUserById(Long id) {
		log.info("Fetching user by id {}", id);
		return userRepo.findById(id).get();
	}
	
	@Override
	public Role getRole(Long id) {
		log.info("Fetching role by id {}", id);
		return roleRepo.findById(id).get();
	}

	@Override
	public Permission getPermission(Long id) {
		log.info("Fetching permission by id {}", id);
		return permissionRepo.findById(id).get();
	}

	@Override
	public List<User> getUsers() {
		log.info("Fetching all users");
		return userRepo.findAll();
	}
	
	@Override
	public List<Role> getRoles() {
		log.info("Fetching all roles");
		return roleRepo.findAll();
	}

	@Override
	public List<Permission> getPermissions() {
		log.info("Fetching all permissions");
		return permissionRepo.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);

		if (user == null) {
			log.error("User not found in the database");
			throw new UsernameNotFoundException("User not found in the database");
		} else {
			log.error("User found in the database: {}", user.getName());
		}
		
		Collection<SimpleGrantedAuthority> authorites = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorites.add(new SimpleGrantedAuthority(role.getName()));
		});

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorites);
	}

}
