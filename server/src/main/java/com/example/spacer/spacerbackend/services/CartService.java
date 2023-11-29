package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CartModel;
import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.repositories.CartRepository;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import com.example.spacer.spacerbackend.repositories.ProductRepository;
import com.example.spacer.spacerbackend.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
  CartRepository cartRepository;
  ClientRepository clientRepository;
  ProductRepository productRepository;

  @Autowired
  public CartService(CartRepository cartRepository, ClientRepository clientRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.clientRepository = clientRepository;
    this.productRepository = productRepository;
  }

  public CartModel[] getAllCarts() {
    try{
      List<CartModel> carts = cartRepository.findAll();
      return carts.toArray(new CartModel[0]);
    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public void newCart(Long productId, Long clientId, Integer quantity) {
    try{
      CartModel cart = new CartModel();
      cart.setQuantity(quantity);
      cart.setProductId(this.productRepository.findById(productId).orElse(null));
      cart.setClientId(this.clientRepository.findById(clientId).orElse(null));
      cartRepository.save(cart);
    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public CartModel findExisting(Long clientId, Long productId) {
    try{
      return cartRepository.findOneByClientIdAndProductId(clientId, productId).orElse(null);
    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public void findAndUpdate(CartModel existingCart, int newQuantity) {
    try{
      int resultQ = existingCart.getQuantity() + newQuantity;
      if (resultQ < 1) {
        this.deleteCart(existingCart.getClientId());
        return;
      }
      existingCart.setQuantity(resultQ);
      cartRepository.save(existingCart);
    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public void createOrUpdate(Long productId, Long clientId, Integer quantity) {
    try {
      if(productId == null){
        throw new CustomException(HttpStatus.BAD_REQUEST, "El producto no existe");
      }else if(clientId == null){
        throw new CustomException(HttpStatus.BAD_REQUEST, "El cliente no existe");
      }else if(quantity == null){
        throw new CustomException(HttpStatus.BAD_REQUEST, "La cantidad no puede ser nula");
      }

      CartModel existingCart = this.findExisting(clientId, productId);

      if(quantity < 1) throw new CustomException(HttpStatus.BAD_REQUEST, "La cantidad no puede ser menor a 1");

      if (existingCart == null) {
        this.newCart(productId, clientId, quantity);
        return;
      }

      this.findAndUpdate(existingCart, quantity);

    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public void decreaseProduct(Long productId, Long clientId) {
    try {
      CartModel existingCart = this.findExisting(clientId, productId);
      if (existingCart != null) {
        this.findAndUpdate(existingCart, -1);
      }else{
        throw new CustomException(HttpStatus.BAD_REQUEST, "El producto no existe en el carrito");
      }
    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public void deleteProduct(Long productId, Long clientId) {
    try {
      CartModel existingCart = this.findExisting(clientId, productId);
      if (existingCart != null) {
        this.deleteCart(existingCart.getClientId());
      }else{
        throw new CustomException(HttpStatus.BAD_REQUEST, "El producto no existe en el carrito");
      }
    } catch(CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "client", key = "#client.username")
  public int deleteCart(ClientModel client) {
    try {
      return cartRepository.deleteCartFromClientId(client.getId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
