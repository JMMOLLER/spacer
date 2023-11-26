package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.repositories.ProductRepository;

import com.example.spacer.spacerbackend.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
  ProductRepository productRepository;

  @Autowired
  public void ProductRepository(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Cacheable(value = "products")
  public ProductModel[] getAllProducts() {
    List<ProductModel> products = productRepository.findAll();
    return products.toArray(new ProductModel[0]);
  }

  @Cacheable(value = "products", key = "#urlprod")
  public ProductModel getProductByUrlProd(String urlprod) {
    try{
      ProductModel product = this.productRepository.findOneByUrlProd(urlprod);

      if(product == null) throw new CustomException(HttpStatus.NOT_FOUND, "Producto no encontrado");

      return product;
    } catch (CustomException e){
      throw new CustomException(e.getStatus(), e.getMessage());
    } catch (Exception e){
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
  }

  @Cacheable(value = "products", key = "#id")
  public ProductModel getProductById(Long id) {
    try{
      Optional<ProductModel> product = this.productRepository.findById(id);
      if(product.isEmpty()){
        throw new CustomException(HttpStatus.NOT_FOUND, "Producto no encontrado");
      }
      return product.get();
    } catch (CustomException e){
      throw new CustomException(e.getStatus(), e.getMessage());
    } catch (Exception e){
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
  }
}
