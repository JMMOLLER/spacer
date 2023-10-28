package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.ClientModel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<ClientModel, Long> {
  @Query("SELECT c FROM ClientModel c WHERE c.username = :username")
  Optional<ClientModel> findOneByUsername(String username);

  List<ClientModel> findAll();

  @Query("SELECT c FROM ClientModel c WHERE c.email = :email")
  Optional<ClientModel> findOneByEmail(String email);
}
