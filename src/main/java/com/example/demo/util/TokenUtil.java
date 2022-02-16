package com.example.demo.util;

import static com.example.demo.constants.SecurityConstants.JWT_KEY_ROLES;
import static com.example.demo.constants.SecurityConstants.JWT_PROVIDER;
import static com.example.demo.constants.SecurityConstants.SECRET;

import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domain.Role;
import com.example.demo.service.UserService;

public class TokenUtil {

	private static final Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());

	public static String accessToken(HttpServletRequest request, Authentication auth, Date date) {
		User userDetails = (User) auth.getPrincipal();
		return JWT.create().withSubject(userDetails.getUsername()).withExpiresAt(date)
				.withIssuer(request.getRequestURL().toString()).withClaim(JWT_KEY_ROLES, userDetails.getAuthorities()
						.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
	}

	public static String refreshToken(HttpServletRequest request, Authentication auth, Date date) {
		User userDetails = (User) auth.getPrincipal();
		return JWT.create().withSubject(userDetails.getUsername()).withExpiresAt(date)
				.withIssuer(request.getRequestURL().toString()).sign(algorithm);
	}

	public static String getToken(HttpServletRequest request, String authorizationHeader, UserService userService, Date date) {
		String username = getDecodeJWT(authorizationHeader);
		com.example.demo.domain.User user = userService.getUser(username);
		
		return JWT.create().withSubject(user.getUsername()).withExpiresAt(date)
				.withIssuer(request.getRequestURL().toString())
				.withClaim(JWT_KEY_ROLES, user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
				.sign(algorithm);
	}

	public static String getDecodeJWT(String authorizationHeader) {
		String token = authorizationHeader.substring(JWT_PROVIDER.length());
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodeJWT = verifier.verify(token);

		return decodeJWT.getSubject();
	}
	
	public static String[] getRoles(String authorizationHeader) {
		String token = authorizationHeader.substring(JWT_PROVIDER.length());
		JWTVerifier verifier = JWT.require(algorithm).build();

		DecodedJWT decodeJWT = verifier.verify(token);
		return decodeJWT.getClaim(JWT_KEY_ROLES).asArray(String.class);
	}
}
