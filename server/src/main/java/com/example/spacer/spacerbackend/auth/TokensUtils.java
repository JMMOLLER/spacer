package com.example.spacer.spacerbackend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokensUtils {
  private final static String ACCESS_TOKEN_SECRET = "sE7e9KY6SDYBqCfRWtL+PuBW6alRsah8UqXcdRRIXx8=";
  private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 5400L;

  public static String createToken(ClientDetailsImp clientDetails) {
    long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
    Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

    Map<String, Object> claims = new HashMap<>();
    claims.put("email", clientDetails.getEmail());
    claims.put("username", clientDetails.getUsername());
    claims.put("userId", clientDetails.getId());
    claims.put("rol", clientDetails.getRol());


    return Jwts.builder().setSubject(clientDetails.getUsername()).setExpiration(expirationDate).addClaims(claims)
      .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes())).compact();
  }

  public static UsernamePasswordAuthenticationToken getAuth(String token) {
    try {
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
      return Jwts.parserBuilder()
        .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (Exception e) {
      return null;
    }
  }
}
