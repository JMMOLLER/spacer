package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.PasswordResetModel;
import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.services.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/")
public class MainController {

  ClientService clientService;
  ProductService productService;
  PasswordResetService passwordResetService;
  MailSenderService mailSenderService;

  @Autowired
  public void ClientController(
    ClientService clientService,
    ProductService productService,
    PasswordResetService passwordResetService,
    MailSenderService mailSenderService)
  {
    this.productService = productService;
    this.clientService = clientService;
    this.passwordResetService = passwordResetService;
    this.mailSenderService = mailSenderService;
  }

  @GetMapping()
  public RedirectView home() {
    return new RedirectView("/api");
  }

  @GetMapping("/api")
  public ResponseEntity<?> apiHome() {
    return new ResponseEntity<>(new Response(HttpStatus.OK, HttpStatus.OK.name(), "Welcome to Spacer API on v1.2.9 🚀!"), HttpStatus.OK);
  }

  @PostMapping("/cliente/reset-password")
  private ResponseEntity<Response> createForgotPasswordRequest(@RequestBody Map<String, String> body) {
    try {

      ClientModel client = this.clientService.getClientByEmail(body.get("email"));

      if (client == null) {
        Response response = new Response(HttpStatus.NOT_FOUND, "El email no existe", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      }

      PasswordResetModel pr = this.passwordResetService.getPrByClientId(client.getId());

      if (pr != null) {
        this.passwordResetService.deleteRequestReset(pr.getId());
      }

      String reqId = this.passwordResetService.createRequestReset(client.getId());

      this.mailSenderService.sendForgotPwd(client.getEmail(), reqId);

      Response response = new Response(HttpStatus.CREATED, HttpStatus.CREATED.name(), null);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/cliente/reset-password/{prId}")
  @ResponseBody
  public ResponseEntity<Response> updatePassword(@RequestBody Map<String, Object> formData, @PathVariable String prId) {
    try {

      if(!Objects.equals(formData.get("new-password").toString(), formData.get("confirm-password").toString())){
        Response response = new Response(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      PasswordResetModel pr = this.passwordResetService.getPrById(prId);

      if(pr == null){
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND, "El link ha expirado", null), HttpStatus.NOT_FOUND);
      }

      ClientModel client = this.clientService.updatePassword(pr.getClientId(), formData.get("new-password").toString());
      this.passwordResetService.deleteRequestReset(prId);
      this.mailSenderService.sendPwdChanged(client.getEmail());

      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), null);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/cliente/{username}.jpg")
  public ResponseEntity<byte[]> getClientImage(@PathVariable String username) {
    ClientModel client = this.clientService.getClientByUsername(username);

    if (client == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    if (client.getImg() == null) {
      try {
        Resource resource = new ClassPathResource("/static/user-default.webp");
        byte[] userImageBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(userImageBytes);
      } catch (IOException e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return getImgResponseEntity(client.getImg());
  }

  @GetMapping("/producto/{urlprod}.jpg")
  public ResponseEntity<byte[]> getProductImage(@PathVariable String urlprod) {
    ProductModel product = this.productService.getProductByUrlProd(urlprod);

    if (product == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

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
