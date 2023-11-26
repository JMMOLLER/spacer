package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.services.ProductService;
import com.example.spacer.spacerbackend.services.Response;
import com.example.spacer.spacerbackend.utils.CustomException;
import com.example.spacer.spacerbackend.utils.UserCredential;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/producto")
public class ProductController {
  ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getProducts() {
    try{
      return new Response(HttpStatus.OK.name(), this.productService.getAllProducts()).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e){
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response> getProductsById(HttpServletRequest request, @PathVariable("id") Long id) {
    try{
      UserCredential credential = new UserCredential(request);

      if(!credential.getRol()) throw new CustomException(HttpStatus.UNAUTHORIZED, "No autorizado");

      return new Response(HttpStatus.OK, HttpStatus.OK.name(), this.productService.getProductById(id)).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e){
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }
}
