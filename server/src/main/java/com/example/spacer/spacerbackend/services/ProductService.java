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
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {
  ProductRepository productRepository;
  private CacheManager cacheManager;
  private final String NBS = String.valueOf((char) 160);

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
      if (product.isEmpty()) throw new CustomException(HttpStatus.NOT_FOUND, "Producto no encontrado");
      return product.get();
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
  }

  @CachePut(value = "products", key = "#result.id")
  public ProductModel updateProduct(ProductModel editedProduct, Long currentProductId) {
    try {
      var currentProduct = this.getProductById(currentProductId);
      updateFieldsChanged(editedProduct, currentProduct);
      clearImgCached(currentProduct.getSimpleUrlImg());
      clearGetAllProductsCache();
      return this.productRepository.save(currentProduct);
    } catch (CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
  }

  private void updateFieldsChanged(ProductModel editedProduct, ProductModel currentProduct) {
    if (containsNoBreakSpace(editedProduct.getMarca())){
      editedProduct.setMarca(editedProduct.getMarca().replaceAll(NBS, " "));
    }if (containsNoBreakSpace(editedProduct.getDescription())) {
      editedProduct.setDescription(editedProduct.getDescription().replaceAll(NBS, " "));
    }

    if (editedProduct.getMarca() != null
      && !editedProduct.getMarca().trim().isEmpty()
      && !editedProduct.getMarca().equals(currentProduct.getMarca())
    ) {
      currentProduct.setMarca(editedProduct.getMarca());
    }
    if (editedProduct.getDescription() != null
      && !editedProduct.getDescription().trim().isEmpty()
      && !editedProduct.getDescription().equals(currentProduct.getDescription())
    ) {
      currentProduct.setDescription(editedProduct.getDescription());
    }
    if (editedProduct.getPrice() != currentProduct.getPrice() && editedProduct.getPrice() >= 0.1) {
      currentProduct.setPrice(editedProduct.getPrice());
    }
    if (!Objects.equals(editedProduct.getCategoryId().getId(), currentProduct.getCategoryId().getId())) {
      currentProduct.setCategoryId(editedProduct.getCategoryId());
    }
    if (editedProduct.getStock() >= 0 && editedProduct.getStock() != currentProduct.getStock()) {
      currentProduct.setStock(editedProduct.getStock());
    }
    if (editedProduct.getImg() != null && !Arrays.equals(editedProduct.getImg(), currentProduct.getImg())) {
      currentProduct.setImg(editedProduct.getImg());
    }
  }

  @CachePut(value = "products", key = "#result.id")
  public ProductModel newProduct(ProductModel newProduct) {
    try {
      validateNewProduct(newProduct);
      clearGetAllProductsCache();
      return this.productRepository.save(newProduct);
    } catch (CustomException e) {
      throw new CustomException(e.getStatus(), e.getMessage());
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void validateNewProduct(ProductModel newProduct){
    if(containsNoBreakSpace(newProduct.getDescription())){
      newProduct.setDescription(newProduct.getDescription().replaceAll(NBS, " "));
    }if(containsNoBreakSpace(newProduct.getMarca())) {
      newProduct.setMarca(newProduct.getMarca().replaceAll(NBS, " "));
    }

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

  public void clearGetAllProductsCache(){
    Cache productsCache = cacheManager.getCache("products");
    if (productsCache != null) {
      productsCache.evict("getAllProducts");
    }
  }

  public void clearImgCached(String urlprod){
    Cache productsCache = cacheManager.getCache("products");
    if (productsCache != null) {
      productsCache.evict(urlprod);
    }
  }

  public boolean containsNoBreakSpace(String str){
    return str.contains(NBS);
  }

  @CacheEvict(value = "products", key = "#id")
  public void deleteProductById(Long id) {
    try {
      ProductModel product = this.getProductById(id);
      clearImgCached(product.getSimpleUrlImg());
      clearGetAllProductsCache();
      this.productRepository.delete(product);
    } catch (CustomException e){
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
