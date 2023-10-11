package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.services.ProductService;
import com.example.spacer.spacerbackend.services.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductController {
  ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getProducts() {
    Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), this.productService.getAllProducts());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
