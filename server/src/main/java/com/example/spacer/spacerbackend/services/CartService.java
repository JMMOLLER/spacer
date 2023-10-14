package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CartModel;
import com.example.spacer.spacerbackend.repositories.CartRepository;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import com.example.spacer.spacerbackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
  CartRepository cartRepository;
  ClientRepository clientRepository;
  ProductRepository productRepository;

  @Autowired
  public void CartRepository(CartRepository cartRepository, ClientRepository clientRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.clientRepository = clientRepository;
    this.productRepository = productRepository;
  }

  public CartModel[] getAllCarts() {
    List<CartModel> carts = cartRepository.findAll();
    return carts.toArray(new CartModel[0]);
  }

  public CartModel newCart(Long productId, Long clientId, Integer quantity) {
    CartModel cart = new CartModel();
    cart.setQuantity(quantity);
    cart.setProductId(this.productRepository.findById(productId).orElse(null));
    cart.setClientId(this.clientRepository.findById(clientId).orElse(null));
    return cartRepository.save(cart);
  }

  public CartModel findExisting(Long clientId, Long productId) {
    return cartRepository.findOneByClientIdAndProductId(clientId, productId).orElse(null);
  }

  public CartModel findAndUpdate(CartModel existingCart, int newQuantity) {
    int resultQ = existingCart.getQuantity() + newQuantity;
    if (resultQ < 1) {
      this.deleteCart(existingCart.getId());
      return null;
    }
    existingCart.setQuantity(resultQ);
    return cartRepository.save(existingCart);
  }

  public void createOrUpdate(Long productId, Long clientId, Integer quantity) {
    try {
      if(productId == null){
        throw new Exception("El producto no existe");
      }else if(clientId == null){
        throw new Exception("El cliente no existe");
      }else if(quantity == null){
        throw new Exception("La cantidad no puede ser nula");
      }

      CartModel existingCart = this.findExisting(clientId, productId);

      if (existingCart != null) {
        this.findAndUpdate(existingCart, quantity);
      }else{

        if(quantity < 1) throw new Exception("La cantidad no puede ser menor a 1");

        this.newCart(productId, clientId, quantity);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteCart(Long id) {
    try {
      cartRepository.deleteById(id);
    } catch (Exception ignored) {
    }
  }
}
