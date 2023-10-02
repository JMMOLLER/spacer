package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.ClientModel;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<ClientModel, Long> {
  Optional<ClientModel> findOneByUsername(String username);
}
