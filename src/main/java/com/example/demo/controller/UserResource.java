package com.example.demo.controller;

import static com.example.demo.constants.SecurityConstants.JWT_PROVIDER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserResource {

	private final UserService userService;

	@GetMapping("/users")
	public ResponseEntity<List<User>> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	
	@GetMapping("/roles")
	public ResponseEntity<List<Role>> getRoles() {
		return ResponseEntity.ok().body(userService.getRoles());
	}

	@GetMapping("/permissions")
	public ResponseEntity<List<Permission>> getPermissions() {
		return ResponseEntity.ok().body(userService.getPermissions());
	}

	@PostMapping("/users/save")
	public ResponseEntity<User> saveUser(@RequestBody User user) {

		/*
		 * URI uri =
		 * URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(
		 * "/api/users/save").toUriString()); return
		 * ResponseEntity.created(uri).body(userService.saveUser(user));
		 */
		User newUser = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
	}

	@PostMapping("/role/save")
	public ResponseEntity<Role> saveRole(@RequestBody Role role) {
		/*
		 * URI uri =
		 * URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(
		 * "/api/role/save").toUriString()); return
		 * ResponseEntity.created(uri).body(userService.saveRole(role));
		 */
		Role newRole = userService.saveRole(role);
		return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
	}

	@PostMapping("/role/addtouser")
	public ResponseEntity<RoleToUserForm> saveRole(@RequestBody RoleToUserForm form) {
		userService.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
	

	@GetMapping(value = "/users/find/{id}")
	public User getUser(@PathVariable("id") Long id) {
		return userService.getUserById(id);
	}
	
	
	@GetMapping(value = "/roles/find/{id}")
	public Role getRole(@PathVariable("id") Long id) {
		return userService.getRole(id);
	}
	
	@GetMapping(value = "/permissions/find/{id}")
	public Permission getPermission(@PathVariable("id") Long id) {
		return userService.getPermission(id);
	}

	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// org.springframework.http.HttpHeaders.AUTHORIZATION;
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith(JWT_PROVIDER)) {

			try {
				String refresh_token = TokenUtil.getToken(request, authorizationHeader, userService, new Date(System.currentTimeMillis() + 10 * 60 * 1000));
			
				Map<String, String> tokens = new HashMap<>();
				tokens.put("refresh_token", refresh_token);
				
				//org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
				response.setContentType(APPLICATION_JSON_VALUE);
				
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
			} catch (Exception e) {
				
				response.setHeader("error", e.getMessage());
				
				//org.springframework.http.HttpStatus.FORBIDDEN
				response.setStatus(FORBIDDEN.value());
			
				
				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());
				
				//org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
				response.setContentType(APPLICATION_JSON_VALUE);
				
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}else {
			throw new RuntimeException("Refresh token in missing");
		}
	
	}

}

@Data
class RoleToUserForm {

	private String username;
	private String roleName;
}
