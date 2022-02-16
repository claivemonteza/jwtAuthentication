package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.domain.Permission;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;

@SpringBootApplication
public class JwtAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthenticationApplication.class, args);
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			
			Permission permission1 = userService.savePermission(new Permission(null, "Ver transações", "View transactions"));
			Permission permission2 = userService.savePermission(new Permission(null, "Criar transação", "Create transaction"));
			Permission permission3 = userService.savePermission(new Permission(null, "Actualizar transação", "Update transaction"));
			Permission permission4 = userService.savePermission(new Permission(null, "Ver perfil", "View profile"));
			Permission permission5 = userService.savePermission(new Permission(null, "Criar perfil", "Create profile"));
			Permission permission6 = userService.savePermission(new Permission(null, "Actualizar perfil", "Update profile"));
			Permission permission7 = userService.savePermission(new Permission(null, "Ver utilizador", "View user"));
			Permission permission8 = userService.savePermission(new Permission(null, "Criar utilizador", "Create user"));
			Permission permission9 = userService.savePermission(new Permission(null, "Actualizar utilizador", "Update User"));

			List<Permission> permissions = new ArrayList<>();
			List<Permission> permissions2 = new ArrayList<>();
			List<Permission> permissions3 = new ArrayList<>();

			
			permissions.add(permission7);
			
			permissions2.add(permission4);
			permissions2.add(permission7);
			
			permissions3.add(permission1);
			permissions3.add(permission2);
			permissions3.add(permission3);

			permissions3.add(permission4);
			permissions3.add(permission5);
			permissions3.add(permission6);

			permissions3.add(permission7);
			permissions3.add(permission8);
			permissions3.add(permission9);
			
			userService.saveRole(new Role(null, "ROLE_USER", permissions, true));

			userService.saveRole(new Role(null, "ROLE_MANAGER", permissions2, true));
			userService.saveRole(new Role(null, "ROLE_ADMIN", permissions3, true));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN", permissions3, true));
			
			userService.saveUser(new User(null, "Donald Trump", "Donald", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "George W. Bush", "Bush", "0000", new ArrayList<>()));
			userService.saveUser(new User(null, "Joe Biden", "Biden", "101006", new ArrayList<>()));
			userService.saveUser(new User(null, "Barack Obama", "Obama", "4321", new ArrayList<>()));
			
			userService.addRoleToUser("Donald", "Role_USER");
			userService.addRoleToUser("Bush", "Role_MANAGER");
			userService.addRoleToUser("Biden", "Role_ADMIN");
			userService.addRoleToUser("Obama", "Role_SUPER_ADMIN");
		};
	}
}
