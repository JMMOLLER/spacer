package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CategoryModel;
import com.example.spacer.spacerbackend.repositories.CategoryRepository;
import com.example.spacer.spacerbackend.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
  CategoryRepository categoryRepository;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
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
}
