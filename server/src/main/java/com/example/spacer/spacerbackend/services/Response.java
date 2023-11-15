package com.example.spacer.spacerbackend.services;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

  public Response(String description, Object response) {
    this.description = description;
    this.response = response;
  }

  public Response(String description) {
    this.description = description;
  }

  public Response() {}

  public ResponseEntity<Response> customResponse() {
    return new ResponseEntity<>(this, HttpStatus.valueOf(this.statusCode));
  }

  public ResponseEntity<Response> badRequestResponse() {
    var httpStatus = HttpStatus.BAD_REQUEST;
    this.statusCode = httpStatus.value();
    if(StringUtils.isBlank(this.description)) this.description = httpStatus.name();
    return new ResponseEntity<>(this, httpStatus);
  }

  public ResponseEntity<Response> unauthorizedResponse() {
    var httpStatus = HttpStatus.UNAUTHORIZED;
    this.statusCode = httpStatus.value();
    if(StringUtils.isBlank(this.description)) this.description = httpStatus.name();
    return new ResponseEntity<>(this, httpStatus);
  }

  public ResponseEntity<Response> okResponse() {
    var httpStatus = HttpStatus.OK;
    this.statusCode = httpStatus.value();
    if(StringUtils.isBlank(this.description)) this.description = httpStatus.name();
    return new ResponseEntity<>(this, httpStatus);
  }

  public ResponseEntity<Response> createdResponse() {
    var httpStatus = HttpStatus.CREATED;
    this.statusCode = httpStatus.value();
    if(StringUtils.isBlank(this.description)) this.description = httpStatus.name();
    return new ResponseEntity<>(this, httpStatus);
  }

  public ResponseEntity<Response> internalServerErrorResponse() {
    var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    this.statusCode = httpStatus.value();
    if(StringUtils.isBlank(this.description)) this.description = httpStatus.name();
    return new ResponseEntity<>(this, httpStatus);
  }

}
