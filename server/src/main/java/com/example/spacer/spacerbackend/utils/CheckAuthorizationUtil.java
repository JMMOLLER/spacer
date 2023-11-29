package com.example.spacer.spacerbackend.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public class CheckAuthorizationUtil {
  public void checkAdmin(HttpServletRequest request) {
    UserCredential credential = new UserCredential(request);
    if(!credential.getRol()) throw new CustomException(HttpStatus.FORBIDDEN, "No tiene los permisos necesarios");
  }
}
