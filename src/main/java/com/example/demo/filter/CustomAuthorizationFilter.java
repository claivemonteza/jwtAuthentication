package com.example.demo.filter;

import static com.example.demo.constants.SecurityConstants.JWT_PROVIDER;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
			filterChain.doFilter(request, response);
		} else {
			// org.springframework.http.HttpHeaders.AUTHORIZATION;
			String authorizationHeader = request.getHeader(AUTHORIZATION);
			if (authorizationHeader != null && authorizationHeader.startsWith(JWT_PROVIDER)) {

				try {
					
					String username = TokenUtil.getDecodeJWT(authorizationHeader);
					String[] roles = TokenUtil.getRoles(authorizationHeader);

					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

					/* java.util.Arrays.stream; */
					stream(roles).forEach(role -> {
						authorities.add(new SimpleGrantedAuthority(role));
					});

					UsernamePasswordAuthenticationToken authenticationToken = 
							new UsernamePasswordAuthenticationToken(username, null, authorities);
					
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);

				} catch (Exception e) {
					// TODO: handle exception
					log.error("Error logging in: {}",e.getMessage());
					response.setHeader("error", e.getMessage());
					
					//org.springframework.http.HttpStatus.FORBIDDEN
					response.setStatus(FORBIDDEN.value());
				//	response.sendError(FORBIDDEN.value());
					
					Map<String, String> error = new HashMap<>();
					error.put("error_message", e.getMessage());
					
					//org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
					response.setContentType(APPLICATION_JSON_VALUE);
					
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}

			}else {
				filterChain.doFilter(request, response);
			}
		}

	}

}
