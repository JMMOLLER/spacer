package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.services.CartService;
import com.example.spacer.spacerbackend.utils.CustomException;
import com.example.spacer.spacerbackend.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carrito")
public class CartController {
  CartService cartService;

  @Autowired
  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getCarts() {
    try{
      return new Response(HttpStatus.OK.name(), cartService.getAllCarts()).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }
}
