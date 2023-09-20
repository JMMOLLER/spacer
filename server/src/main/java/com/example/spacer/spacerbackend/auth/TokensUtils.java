package com.example.spacer.spacerbackend.auth;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class TokensUtils {
  private final static String ACCESS_TOKEN_SECRET = "sE7e9KY6SDYBqCfRWtL+PuBW6alRsah8UqXcdRRIXx8=";
  private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 3600L;

  public static String createToken(String username, String email) {
    long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
    Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", email);
    claims.put("username", username);


    return Jwts.builder().setSubject(username).setExpiration(expirationDate).addClaims(claims)
        .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes())).compact();
  }

  public static UsernamePasswordAuthenticationToken getAuth(String token) {
    try{
      Claims claims = Jwts.parserBuilder().setSigningKey(ACCESS_TOKEN_SECRET.getBytes()).build().parseClaimsJws(token)
          .getBody();
      String username = claims.getSubject();

      return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    } catch (JwtException e) {
      return null;
    }
  }

  public static Map<String, Object> getPayloadFromToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
              .build()
              .parseClaimsJws(token)
              .getBody();

      return claims;
    } catch (Exception e) {
      // Manejo de excepción si ocurre un error al decodificar el token
      return null;
    }
  }
}
