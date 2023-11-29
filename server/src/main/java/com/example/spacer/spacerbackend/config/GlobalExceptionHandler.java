package com.example.spacer.spacerbackend.config;

import com.example.spacer.spacerbackend.services.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<Response> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
    return new Response(HttpStatus.BAD_REQUEST.name(), null).customResponse(HttpStatus.BAD_REQUEST);
  }
}
