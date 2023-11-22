package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.PasswordResetModel;
import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.services.*;
import io.micrometer.common.util.StringUtils;
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
import java.util.concurrent.CompletableFuture;

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
    return new ResponseEntity<>(new Response(HttpStatus.OK, HttpStatus.OK.name(), "Welcome to Spacer API on v1.4.4 🚀!"), HttpStatus.OK);
  }

  @PostMapping("/cliente/reset-password")
  private ResponseEntity<Response> createForgotPasswordRequest(@RequestBody Map<String, String> body,
                                                               @RequestParam(required = false) String consultCode) {
    try {
      String email = body.get("email");

      if (StringUtils.isBlank(email)) {
        return new Response("El email no puede estar vacío").badRequestResponse();
      }

      if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
        return new Response("El email no es válido").badRequestResponse();
      }

      ClientModel client = this.clientService.getClientByEmail(email);

      if (consultCode != null && client == null) {
        return new Response("Código no reconocido. No autorizado a cambio de contraseña").unauthorizedResponse();
      }

      if (client == null) {
        return new Response().okResponse(); // Se retorna un 200 OK para evitar que se sepa si el email existe o no
      }

      if (consultCode != null) {
        PasswordResetModel pr = this.passwordResetService.getPrByCode(consultCode.toLowerCase(), client.getId());

        if (pr == null || !pr.getId().toUpperCase().startsWith(consultCode)) {
          return new Response("Código no reconocido. No autorizado a cambio de contraseña").unauthorizedResponse();
        } else {
          return new Response().okResponse(); // Se retorna un 200 OK para evitar que se sepa si el email existe o no
        }
      }

      CompletableFuture.runAsync(() -> {
        try {
          PasswordResetModel pr = this.passwordResetService.getPrByClientId(client.getId());

          if (pr != null) {
            this.passwordResetService.deleteRequestReset(pr.getId());
          }

          String reqId = this.passwordResetService.createRequestReset(client.getId());

          this.mailSenderService.sendForgotPwd(client.getEmail(), reqId.substring(0, 5).toUpperCase());
        } catch (Exception e) {
          e.printStackTrace(); // Por temas prácticos, no se está utilizando un logger
        }
      }); // Se ejecuta en un hilo aparte como tarea asíncrona (tipo JavaScript <3)

      return new Response().okResponse(); // Se retorna un 200 OK para evitar que se sepa si el email existe o no
    } catch (Exception e) {
      var message = ExceptionUtils.getRootCause(e).getMessage();
      return new Response(message).internalServerErrorResponse();
    }
  }

  @PutMapping("/cliente/reset-password/{code}")
  @ResponseBody
  public ResponseEntity<Response> updatePassword(@RequestBody Map<String, String> formData, @PathVariable String code) {
    try {

      if(formData.get("new-password") == null || formData.get("confirm-password") == null){
        Response response = new Response(HttpStatus.BAD_REQUEST, "Las contraseñas no pueden estar vacías", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      if(formData.get("new-password").trim().length() < 8){
        Response response = new Response(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      if(!formData.get("new-password").equals(formData.get("confirm-password"))){
        Response response = new Response(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      if(formData.get("email") == null){
        Response response = new Response(HttpStatus.BAD_REQUEST, "El email no puede estar vacío", null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }

      ClientModel cli = this.clientService.getClientByEmail(formData.get("email"));

      PasswordResetModel pr = this.passwordResetService.getPrByCode(code.toLowerCase(), cli.getId());

      if(pr == null){
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND, "El link ha expirado", null), HttpStatus.NOT_FOUND);
      }

      ClientModel client = this.clientService.updatePassword(cli, formData.get("new-password"));
      this.passwordResetService.deleteRequestReset(pr.getId());
      this.mailSenderService.sendPwdChanged(client.getEmail());

      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), null);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
