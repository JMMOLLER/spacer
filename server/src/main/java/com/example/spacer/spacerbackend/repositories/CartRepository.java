package com.example.spacer.spacerbackend.repositories;

import java.util.List;
import com.example.spacer.spacerbackend.models.CartModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<CartModel, Long> {
  @Query("SELECT c FROM CartModel c WHERE c.clientId.id = ?1 AND c.productId.id = ?2")
  Optional<CartModel> findOneByClientIdAndProductId(Long clientId, Long productId);

  List<CartModel> findAll();
}
