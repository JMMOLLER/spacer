package com.example.spacer.spacerbackend.auth;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain)
    throws ServletException, IOException {

    String auth = request.getHeader("Authorization");

    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.replace("Bearer ", "");
      UsernamePasswordAuthenticationToken usernamePAT = TokensUtils.getAuth(token);
      SecurityContextHolder.getContext().setAuthentication(usernamePAT);
    }
    assert filterChain != null; // AssertionError verifica que no sea null
    filterChain.doFilter(request, response);
  }

}
