package com.example.demo.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		log.info("Username is: {}",username);
		log.info("Password is: {}",password);
		
		UsernamePasswordAuthenticationToken uat = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(uat);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		String access_token = TokenUtil.accessToken(request, auth,
				new Date(System.currentTimeMillis() + 10 * 60 * 1000));
		
		String refresh_token = TokenUtil.refreshToken(request, auth,
				new Date(System.currentTimeMillis() + 30 * 60 * 1000));
		
		/*response.setHeader("access_token", access_token);
		response.setHeader("refresh_token", refresh_token);*/
		
		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		
		//org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
		response.setContentType(APPLICATION_JSON_VALUE);
		
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

}
