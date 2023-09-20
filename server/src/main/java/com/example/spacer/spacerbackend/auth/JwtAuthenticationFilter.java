package com.example.spacer.spacerbackend.auth;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

    AuthCredentials credentials = new AuthCredentials();

    try {
      credentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
    } catch (Exception ignored) {
    }

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(credentials.getUsername(),
        credentials.getPassword(), Collections.emptyList());

    return getAuthenticationManager().authenticate(authToken);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {

    ClientDetailsImp clientDetails = (ClientDetailsImp) authResult.getPrincipal();
    String token = TokensUtils.createToken(clientDetails.getUsername(), clientDetails.getEmail());
    Map<String, Object> payload = TokensUtils.getPayloadFromToken(token);

    response.addHeader("Authorization", "Bearer " + token);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("{\"token\": \"" + token + "\", \"payload\":" + new ObjectMapper().writeValueAsString(payload) + "}");

    response.getWriter().flush();

    super.successfulAuthentication(request, response, chain, authResult);
  }

}
