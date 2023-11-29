package com.example.spacer.spacerbackend.auth;

import com.example.spacer.spacerbackend.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

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
    String token = TokensUtils.createToken(clientDetails);
    Map<String, Object> payload = TokensUtils.getPayloadFromToken(token);
    JSONObject json = new JSONObject();
    json.put("token", token);
    json.put("payload", payload);
    Response res = new Response(HttpStatus.OK, HttpStatus.OK.name(), json.toMap());

    response.addHeader("Authorization", "Bearer " + token);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(new ObjectMapper().writeValueAsString(res));

    response.getWriter().flush();

    super.successfulAuthentication(request, response, chain, authResult);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException failed) throws IOException {
    Response res = new Response(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.name(), null);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(new ObjectMapper().writeValueAsString(res));
    response.getWriter().flush();
  }

}
