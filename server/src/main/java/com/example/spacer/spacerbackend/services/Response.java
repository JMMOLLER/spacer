package com.example.spacer.spacerbackend.services;

import org.springframework.http.HttpStatus;

public class Response {
  private Integer statusCode;
  private String description;
  private Object response;

  public Response(HttpStatus statusCode, String message, Object response) {
    this.statusCode = statusCode.value();
    this.description = message;
    this.response = response;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String message) {
    this.description = message;
  }

  public Object getResponse() {
    return response;
  }

  public void setResponse(Object response) {
    this.response = response;
  }

  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }
}
