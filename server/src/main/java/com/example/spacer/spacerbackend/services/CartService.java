package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CartModel;
import com.example.spacer.spacerbackend.repositories.CartRepository;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import com.example.spacer.spacerbackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

  public void newCart(Long productId, Long clientId, Integer quantity) {
    CartModel cart = new CartModel();
    cart.setQuantity(quantity);
    cart.setProductId(this.productRepository.findById(productId).orElse(null));
    cart.setClientId(this.clientRepository.findById(clientId).orElse(null));
    cartRepository.save(cart);
  }

  public CartModel findExisting(Long clientId, Long productId) {
    return cartRepository.findOneByClientIdAndProductId(clientId, productId).orElse(null);
  }

  public void findAndUpdate(CartModel existingCart, int newQuantity) {
    int resultQ = existingCart.getQuantity() + newQuantity;
    if (resultQ < 1) {
      this.deleteCart(existingCart.getId());
      return;
    }
    existingCart.setQuantity(resultQ);
    cartRepository.save(existingCart);
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

      if(quantity < 1) throw new Exception("La cantidad no puede ser menor a 1");

      if (existingCart == null) {
        this.newCart(productId, clientId, quantity);
        return;
      }

      this.findAndUpdate(existingCart, quantity);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void decreaseProduct(Long productId, Long clientId) {
    try {
      CartModel existingCart = this.findExisting(clientId, productId);
      if (existingCart != null) {
        this.findAndUpdate(existingCart, -1);
      }else{
        throw new Exception("El producto no existe en el carrito");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteProduct(Long productId, Long clientId) {
    try {
      CartModel existingCart = this.findExisting(clientId, productId);
      if (existingCart != null) {
        this.deleteCart(existingCart.getId());
      }else{
        throw new Exception("El producto no existe en el carrito");
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
