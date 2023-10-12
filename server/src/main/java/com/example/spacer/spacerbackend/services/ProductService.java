package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {
  ProductRepository productRepository;

  @Autowired
  public void ProductRepository(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public ArrayList<Object> getAllProducts() {
    ArrayList<ProductModel> products = (ArrayList<ProductModel>) productRepository.findAll();
    ArrayList<Object> productsSummary = new ArrayList<>();
    for (ProductModel product : products) {
      try {
        productsSummary.add(new FilterImg(product).getFilteredObject());
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    return productsSummary;
  }

  public ProductModel getProductById(String id) {
    return this.productRepository.findById(Long.valueOf(id)).orElse(null);
  }
}
