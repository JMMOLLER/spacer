package com.example.spacer.spacerbackend.repositories;

import java.util.List;

import com.example.spacer.spacerbackend.models.ProductModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductModel, Long> {
  @NonNull
  List<ProductModel> findAll();

  @Query("SELECT pr FROM ProductModel pr WHERE pr.urlImg = :urlprod")
  ProductModel findOneByUrlProd(String urlprod);
}
