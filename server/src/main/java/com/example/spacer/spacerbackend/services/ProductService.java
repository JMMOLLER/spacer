package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
  ProductRepository productRepository;

  @Autowired
  public void ProductRepository(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public ProductModel[] getAllProducts() {
    List<ProductModel> products = productRepository.findAll();
    return products.toArray(new ProductModel[0]);
  }

  public ProductModel getProductByUrlProd(String urlprod) {
    return this.productRepository.findOneByUrlProd(urlprod);
  }
}
