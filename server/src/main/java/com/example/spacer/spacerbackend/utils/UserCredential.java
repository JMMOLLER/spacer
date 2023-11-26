package com.example.spacer.spacerbackend.utils;

import com.example.spacer.spacerbackend.auth.TokensUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UserCredential {
  private final String username;
  private final Long userId;
  private final Boolean rol;

  public UserCredential(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");
    Map<String, Object> payload = TokensUtils.getPayloadFromToken(
      authorizationHeader.replace("Bearer ", "")
    );
    assert payload != null;
    this.username = payload.get("username").toString();
    this.userId = Long.valueOf(payload.get("userId").toString());
    this.rol = Boolean.valueOf(payload.get("rol").toString());
  }
}
