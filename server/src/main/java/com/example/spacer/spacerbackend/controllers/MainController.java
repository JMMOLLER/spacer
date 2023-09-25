package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.services.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainController {

  @GetMapping()
  public ResponseEntity<?> home() {
    return new ResponseEntity<>(new Response(HttpStatus.OK, HttpStatus.OK.name(), "Welcome to Spacer API 🚀!"), HttpStatus.OK);
  }
}
