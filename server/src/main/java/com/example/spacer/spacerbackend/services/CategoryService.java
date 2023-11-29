package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CategoryModel;
import com.example.spacer.spacerbackend.repositories.CategoryRepository;
import com.example.spacer.spacerbackend.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  CategoryRepository categoryRepository;
  CacheManager cacheManager;
  private final String NBS = String.valueOf((char) 160);

  @Autowired
  public CategoryService(CategoryRepository categoryRepository, CacheManager cacheManager) {
    this.categoryRepository = categoryRepository;
    this.cacheManager = cacheManager;
  }

  @Cacheable(value = "categories", key = "#root.method.name")
  public CategoryModel[] getAllCategories() {
    try {
      var categories = this.categoryRepository.findAll();
      return categories.toArray(new CategoryModel[0]);
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Cacheable(value = "category", key = "#id")
  public CategoryModel getCategoryById(Long id) {
    try {
      var cat = this.categoryRepository.findById(id);
      if(cat.isEmpty()) throw new CustomException(HttpStatus.NOT_FOUND, "Categoria no encontrada");
      return cat.get();
    } catch (CustomException e){
      throw e;
    }catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CachePut(value = "category", key = "#result.id")
  public CategoryModel newCategory(CategoryModel newCategory) {
    try {
      validateNewCategory(newCategory);
      this.clearGetAllCategoriesCache();
      if(newCategory.getId() != null) newCategory.setId(null);
      return this.categoryRepository.save(newCategory);
    } catch (CustomException e){
      throw e;
    }catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CachePut(value = "category", key = "#result.id")
  public CategoryModel updateCategory(CategoryModel category, String newName) {
    try {
      category.setName(newName);
      validateNewCategory(category);
      this.clearGetAllCategoriesCache();
      return this.categoryRepository.save(category);
    } catch (CustomException e){
      throw e;
    }catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "category", key = "#category.id")
  public void deleteCategory(CategoryModel category) {
    try {
      this.clearGetAllCategoriesCache();
      this.categoryRepository.delete(category);
    } catch (CustomException e){
      throw e;
    }catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void validateNewCategory(CategoryModel category) {
    if(category.getName() == null) throw new CustomException(HttpStatus.BAD_REQUEST, "Debe enviar el nombre de la nueva categoria");

    if (this.containsNoBreakSpace(category.getName())) {
      category.setName(category.getName().replaceAll(NBS, " "));
    }

    if (category.getName().isEmpty() || category.getName().isBlank()) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "El nombre de la categoria no puede estar vacio");
    }
  }

  public boolean containsNoBreakSpace(String str){
    return str.contains(NBS);
  }

  private void clearGetAllCategoriesCache() {
    Cache cache = this.cacheManager.getCache("categories");
    if (cache != null) {
      cache.evict("getAllCategories");
    }
  }

}
