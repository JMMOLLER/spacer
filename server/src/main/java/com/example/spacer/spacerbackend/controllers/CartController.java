package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.CartModel;
import com.example.spacer.spacerbackend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/carrito")
public class CartController {
  CartService cartService;

  @Autowired
  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping("/all")
  public CartModel[] getCarts() {
    return cartService.getAllCarts();
  }
}
