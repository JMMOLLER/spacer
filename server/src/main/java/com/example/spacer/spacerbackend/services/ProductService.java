package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.repositories.ProductRepository;

import com.example.spacer.spacerbackend.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
  ProductRepository productRepository;
  private CacheManager cacheManager;

  @Autowired
  public void ProductRepository(ProductRepository productRepository, CacheManager cacheManager) {
    this.productRepository = productRepository;
    this.cacheManager = cacheManager;
  }

  @Cacheable(value = "products", key = "#root.method.name")
  public ProductModel[] getAllProducts() {
    List<ProductModel> products = productRepository.findAll();
    return products.toArray(new ProductModel[0]);
  }

  @Cacheable(value = "products", key = "#urlprod")
  public ProductModel getProductByUrlProd(String urlprod) {
    try {
      ProductModel product = this.productRepository.findOneByUrlProd(urlprod);

      if (product == null) throw new CustomException(HttpStatus.NOT_FOUND, "Producto no encontrado");

      return product;
    } catch (CustomException e) {
      throw new CustomException(e.getStatus(), e.getMessage());
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
  }

  @Cacheable(value = "products", key = "#id")
  public ProductModel getProductById(Long id) {
    try {
      Optional<ProductModel> product = this.productRepository.findById(id);
      if (product.isEmpty()) {
        throw new CustomException(HttpStatus.NOT_FOUND, "Producto no encontrado");
      }
      return product.get();
    } catch (CustomException e) {
      throw new CustomException(e.getStatus(), e.getMessage());
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
  }
  @CacheEvict(value = "products", key = "#key")
  public ProductModel newProduct(ProductModel newProduct, String key) {
    try {
      validateNewProduct(newProduct);
      return this.productRepository.save(newProduct);
    } catch (CustomException e) {
      throw new CustomException(e.getStatus(), e.getMessage());
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void validateNewProduct(ProductModel newProduct){
    if(newProduct.getMarca() == null || newProduct.getMarca().isEmpty()){
      throw new CustomException(HttpStatus.BAD_REQUEST, "La marca no puede estar vacia");
    }
    if(newProduct.getDescription() == null || newProduct.getDescription().isEmpty()){
      throw new CustomException(HttpStatus.BAD_REQUEST, "La descripcion no puede estar vacia");
    }
    if(newProduct.getPrice() < 0.1){
      throw new CustomException(HttpStatus.BAD_REQUEST, "El precio no puede ser menor a 0.1");
    }
    if(newProduct.getCategoryId() == null){
      throw new CustomException(HttpStatus.BAD_REQUEST, "La categoria no puede estar vacia");
    }
    if(newProduct.getStock() < 1){
      throw new CustomException(HttpStatus.BAD_REQUEST, "El stock no puede ser menor a 1");
    }
    if(newProduct.getImg() == null || newProduct.getImg().length == 0){
      throw new CustomException(HttpStatus.BAD_REQUEST, "La imagen no puede estar vacia");
    }
  }

}
