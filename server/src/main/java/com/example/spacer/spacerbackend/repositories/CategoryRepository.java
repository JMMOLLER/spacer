package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.CategoryModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CategoryRepository extends CrudRepository<CategoryModel, Long> {
  @NonNull
  List<CategoryModel> findAll();
}
