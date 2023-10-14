package com.example.spacer.spacerbackend.repositories;

import java.util.List;
import java.util.ArrayList;
import com.example.spacer.spacerbackend.models.CartModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<CartModel, Long> {
  default Optional<CartModel> findOneByClientIdAndProductId(Long clientId, Long productId) {
    Optional<Iterable<CartModel>> carts = this.findAllByClientId(clientId);
    if(carts.isPresent()){
      for (CartModel cartModel : carts.get()) {
        if (cartModel.getProductId().getId().equals(productId)) {
          return Optional.of(cartModel);
        }
      }
    }
    return Optional.empty();
  }

  default Optional<Iterable<CartModel>> findAllByClientId(Long clientId) {
    List<CartModel> matchingCarts = new ArrayList<>();
    Iterable<CartModel> carts = this.findAll();
    for (CartModel cartModel : carts) {
      if (cartModel.getClientId().getId().equals(clientId)) {
        matchingCarts.add(cartModel);
      }
    }
    return Optional.of(matchingCarts);
  }

  List<CartModel> findAll();
}
