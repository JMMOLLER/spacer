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
        productsSummary.add(new ProductService().getProductInfoSummary(product));
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    return productsSummary;
  }

  public Map<String, Object> getProductInfoSummary(ProductModel productModel) throws IllegalAccessException {
    Map<String, Object> product = new HashMap<>();

    Field[] fields = productModel.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      if (field.getName().equals("img"))
        continue;
      String fieldName = field.getName();
      Object value = field.get(productModel);
      product.put(fieldName, value);
    }

    return product;
  }
}
