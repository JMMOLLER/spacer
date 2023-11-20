package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.InvoiceModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends CrudRepository<InvoiceModel, Long> {
  @NonNull
  @Query("SELECT i FROM InvoiceModel i ORDER BY i.id DESC")
  List<InvoiceModel> findAll();

  @Query("SELECT i FROM InvoiceModel i WHERE i.clientId = :clientId")
  List<InvoiceModel> findByClientId(Long clientId);
}
