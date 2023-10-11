package com.example.spacer.spacerbackend.repositories;

import java.util.Optional;
import com.example.spacer.spacerbackend.models.ProductModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductModel, Long> {
  Optional<ProductModel> findByMarca(String marca);
}
