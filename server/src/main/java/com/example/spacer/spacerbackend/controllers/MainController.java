package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.services.ClientService;
import com.example.spacer.spacerbackend.services.ProductService;
import com.example.spacer.spacerbackend.services.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public class MainController {

  ClientService clientService;
  ProductService productService;

  @Autowired
  public void ClientController(ClientService clientService, ProductService productService) {
    this.productService = productService;
    this.clientService = clientService;
  }

  @GetMapping()
  //redirect to /api
  public RedirectView home() {
    return new RedirectView("/api");
  }

  @GetMapping("/api")
  public ResponseEntity<?> apiHome() {
    return new ResponseEntity<>(new Response(HttpStatus.OK, HttpStatus.OK.name(), "Welcome to Spacer API 🚀!"), HttpStatus.OK);
  }

  @GetMapping("/cliente/{username}.jpg")
  public ResponseEntity<byte[]> getClientImage(@PathVariable String username) {
    ClientModel client = this.clientService.getClientByUsername(username);

    assert client != null;
    return getImgResponseEntity(client.getImg());
  }

  @GetMapping("/producto/{id}.jpg")
  public ResponseEntity<byte[]> getProductImage(@PathVariable String id) {
    ProductModel product = this.productService.getProductById(id);

    if(product == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return getImgResponseEntity(product.getImg());
  }

  @GetMapping("/**")
  public ResponseEntity<?> notFound() {
    return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.name(), "Not found"), HttpStatus.NOT_FOUND);
  }

  private ResponseEntity<byte[]> getImgResponseEntity(byte[] img) {
    if (img != null) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.IMAGE_JPEG);
      headers.setContentLength(img.length);

      return new ResponseEntity<>(img, headers, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
