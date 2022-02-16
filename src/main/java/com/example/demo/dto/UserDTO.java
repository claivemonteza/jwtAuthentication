package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;

import lombok.Data;

@Data
public class UserDTO {

	private Long id;

	private String name;

	private String username;

	private String password;
	
	private List<Long> rolesId;
	
	public User toEntity() {
		List<Role> roles = new ArrayList<>();
		rolesId.forEach(id->{
			Role role = new Role();
			role.setId(id);
			roles.add(role);
		});
		return new User(null, name, username, password, roles);
	}
	
	
	public User toEntity(Long id) {
		List<Role> roles = new ArrayList<>();
		rolesId.forEach(roleId->{
			Role role = new Role();
			role.setId(roleId);
			roles.add(role);
		});
		return new User(id, name, username, password, roles);
	}
}
