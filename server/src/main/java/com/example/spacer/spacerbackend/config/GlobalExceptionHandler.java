package com.example.spacer.spacerbackend.config;

import com.example.spacer.spacerbackend.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<Response> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
    return new Response("Bad Request", null).badRequestResponse();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Response> handleInternalError(Exception ex) {
    return new Response("Internal Server Error", null).internalServerErrorResponse();
  }
}
