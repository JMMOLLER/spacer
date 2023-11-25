package com.example.spacer.spacerbackend.repositories;

import java.util.List;
import com.example.spacer.spacerbackend.models.CartModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<CartModel, Long> {
  @NonNull
  List<CartModel> findAll();

  @Query("SELECT c FROM CartModel c WHERE c.clientId.id = :clientId AND c.productId.id = :productId")
  Optional<CartModel> findOneByClientIdAndProductId(Long clientId, Long productId);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM CartModel c WHERE c.clientId.id = :clientId")
  int deleteCartFromClientId(Long clientId);

}
