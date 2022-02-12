package com.example.demo.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
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

	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// org.springframework.http.HttpHeaders.AUTHORIZATION;
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

			try {
				String token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();

				DecodedJWT decodeJWT = verifier.verify(token);

				String username = decodeJWT.getSubject();
				User user = userService.getUser(username);

				String refresh_token = JWT.create().withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);
			
				
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
