package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.ProductInvoiceModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInvoiceRepository extends CrudRepository<ProductInvoiceModel, Long> {
}
