package com.example.spacer.spacerbackend.services;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class Response {
  @lombok.Setter
  private Integer statusCode;
  @lombok.Setter
  private String description;
  @lombok.Setter
  private Object response;

  public Response(HttpStatus statusCode, String message, Object response) {
    this.statusCode = statusCode.value();
    this.description = message;
    this.response = response;
  }

}
