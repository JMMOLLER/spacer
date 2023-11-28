package com.example.spacer.spacerbackend.repositories;

import com.example.spacer.spacerbackend.models.CategoryModel;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<CategoryModel, Long> {}
