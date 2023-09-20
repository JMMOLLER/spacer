package com.example.spacer.spacerbackend.auth;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class AuthConfig {

  private final ClientDetailsServices clientDetailsService;
  private final JwtAuthorizationFilter jwtAuthorizationFilter;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

    JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter();
    jwtAuthFilter.setAuthenticationManager(authManager);
    jwtAuthFilter.setFilterProcessesUrl("/api/auth");

    return http
      .csrf().disable()
      .authorizeRequests(authorizeRequests ->
        authorizeRequests
        .anyRequest().authenticated()
      )
      .sessionManagement(sessionManagement ->
        sessionManagement
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();

  }

  @Bean
  AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(clientDetailsService)
      .passwordEncoder(passwordEncoder).and().build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
