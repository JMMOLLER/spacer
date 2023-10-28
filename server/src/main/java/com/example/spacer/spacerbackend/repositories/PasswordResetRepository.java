package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.PasswordResetModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends CrudRepository<PasswordResetModel, String> {

  @Query("SELECT pr FROM PasswordResetModel pr WHERE pr.clientId = :clientId")
  Optional<PasswordResetModel> findOneByClientId(@Param("clientId") Long clientId);
}
