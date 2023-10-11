package com.example.spacer.spacerbackend.auth;


import com.example.spacer.spacerbackend.services.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
      .cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
      .exceptionHandling(exceptionHandling ->
        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
          response.setContentType("application/json;charset=UTF-8");
          response.setStatus(HttpStatus.FORBIDDEN.value());
          Response res = new Response(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.name(), null);
          response.getWriter().write(new ObjectMapper().writeValueAsString(res));
        })
      )
      .authorizeHttpRequests(authorizeRequests ->
        authorizeRequests
        .requestMatchers("*").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/cliente").permitAll()
        .requestMatchers("/api/auth").permitAll()
        .requestMatchers("/api/**").authenticated()
        .anyRequest().permitAll()
      ).sessionManagement(sessionManagement ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      ).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
      .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration conf = new CorsConfiguration();
    conf.setAllowedOrigins(java.util.List.of("*"));
    conf.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    conf.setAllowedHeaders(java.util.List.of("Authorization", "Cache-Control", "Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", conf);
    return source;
  }

  @Bean
  AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(clientDetailsService).passwordEncoder(passwordEncoder);
    return authenticationManagerBuilder.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
