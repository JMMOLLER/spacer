package com.example.spacer.spacerbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpacerBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpacerBackendApplication.class, args);
  }

}
