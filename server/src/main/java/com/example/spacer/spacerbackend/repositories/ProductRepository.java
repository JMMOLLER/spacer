package com.example.spacer.spacerbackend.repositories;

import java.util.ArrayList;
import java.util.List;

import com.example.spacer.spacerbackend.models.ProductModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<ProductModel, Long> {
  List<ProductModel> findAll();

  default ProductModel findOneByUrlProd(String urlprod){
    List<ProductModel> products = this.findAll();
    for(ProductModel product : products){
      if(product.getUrlImg().equals(urlprod)) return product;
    }
    return null;
  };
}
