package com.example.spacer.spacerbackend.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String auth = request.getHeader("Authorization");

    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.replace("Bearer ", "");
      UsernamePasswordAuthenticationToken usernamePAT = TokensUtils.getAuth(token);
      SecurityContextHolder.getContext().setAuthentication(usernamePAT);
    }
    filterChain.doFilter(request, response);
  }

}
