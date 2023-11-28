package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.CategoryModel;
import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.services.CategoryService;
import com.example.spacer.spacerbackend.services.ProductService;
import com.example.spacer.spacerbackend.services.Response;
import com.example.spacer.spacerbackend.utils.CustomException;
import com.example.spacer.spacerbackend.utils.UserCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/producto")
public class ProductController {
  ProductService productService;
  CategoryService categoryService;

  @Autowired
  public ProductController(ProductService productService, CategoryService categoryService) {
    this.productService = productService;
    this.categoryService = categoryService;
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getProducts() {
    try{
      return new Response(HttpStatus.OK.name(), this.productService.getAllProducts()).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e){
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Response> getProductsById(HttpServletRequest request, @PathVariable("id") Long id) {
    try{
      UserCredential credential = new UserCredential(request);

      if(!credential.getRol()) throw new CustomException(HttpStatus.FORBIDDEN, "No tiene los permisos necesarios");

      return new Response(HttpStatus.OK, HttpStatus.OK.name(), this.productService.getProductById(id)).okResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e){
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

  @PostMapping()
  public ResponseEntity<Response> createProducts(HttpServletRequest request,
                                                 @RequestPart(value = "img") MultipartFile img,
                                                 @RequestParam Map<String, Object> formDataJson) {
    try{
      UserCredential credential = new UserCredential(request);

      if(!credential.getRol()) throw new CustomException(HttpStatus.FORBIDDEN, "No tiene los permisos necesarios");

      ObjectMapper objectMapper = new ObjectMapper();
      ProductModel newProduct = objectMapper.convertValue(formDataJson, ProductModel.class);

      newProduct.setCategoryId(this.categoryService.getCategoryById(newProduct.getCategoryId().getId()));
      newProduct.setImg(img.getBytes());

      var created = this.productService.newProduct(newProduct);

      return new Response(HttpStatus.CREATED.name(), created).createdResponse();
    } catch (CustomException e){
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e){
      return new Response(e.getMessage()).internalServerErrorResponse();
    }
  }

}
