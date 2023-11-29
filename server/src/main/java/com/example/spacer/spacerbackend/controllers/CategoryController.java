package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.CategoryModel;
import com.example.spacer.spacerbackend.services.CategoryService;
import com.example.spacer.spacerbackend.utils.CheckAuthorizationUtil;
import com.example.spacer.spacerbackend.utils.CustomException;
import com.example.spacer.spacerbackend.utils.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/categoria")
public class CategoryController {
  CategoryService categoryService;
  CheckAuthorizationUtil checkAuthorization = new CheckAuthorizationUtil();

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getAllCategories(HttpServletRequest req) {
    try {
      checkAuthorization.checkAdmin(req);
      var categories = this.categoryService.getAllCategories();
      return new Response(HttpStatus.OK.name(), categories).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response> getCategoryById(HttpServletRequest req, @PathVariable Long id) {
    try {
      checkAuthorization.checkAdmin(req);
      var category = this.categoryService.getCategoryById(id);
      return new Response(HttpStatus.OK.name(), category).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @PostMapping()
  public ResponseEntity<Response> newCategory(HttpServletRequest req, @RequestBody(required = false) CategoryModel body) {
    try {
      checkAuthorization.checkAdmin(req);

      if(body == null) throw new CustomException(HttpStatus.BAD_REQUEST, "Debe enviar el cuerpo de la solicitud.");

      var newCategory = this.categoryService.newCategory(body);
      return new Response(HttpStatus.CREATED.name(), newCategory).createdResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      System.out.println(e.getCause().getMessage());
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Response> updateCategory(HttpServletRequest req,
                                                 @PathVariable Long id,
                                                 @RequestBody(required = false) CategoryModel body
  ) {
    try {
      checkAuthorization.checkAdmin(req);

      if(body == null) throw new CustomException(HttpStatus.BAD_REQUEST, "Debe enviar el cuerpo de la solicitud.");

      var currentCategory = this.categoryService.getCategoryById(id);
      var updatedCategory = this.categoryService.updateCategory(currentCategory, body.getName());
      return new Response(HttpStatus.OK.name(), updatedCategory).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Response> deleteCategory(HttpServletRequest req, @PathVariable Long id) {
    try {
      checkAuthorization.checkAdmin(req);
      var currentCategory = this.categoryService.getCategoryById(id);
      this.categoryService.deleteCategory(currentCategory);
      return new Response(HttpStatus.OK.name()).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }
}
