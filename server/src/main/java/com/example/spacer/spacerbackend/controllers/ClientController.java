package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.auth.TokensUtils;
import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.services.ClientService;
import com.example.spacer.spacerbackend.services.MailSenderService;
import com.example.spacer.spacerbackend.services.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
public class ClientController {
  ClientService clientService;
  MailSenderService mailSenderService;

  @Autowired
  public ClientController(ClientService clientService, MailSenderService mailSenderService) {
    this.clientService = clientService;
    this.mailSenderService = mailSenderService;
  }

  @GetMapping()
  public ResponseEntity<Response> getClientByUsername(HttpServletRequest request) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      Map<String, Object> payload = TokensUtils.getPayloadFromToken(
        authorizationHeader.replace("Bearer ", "")
      );
      assert payload != null;
      ClientModel cs = this.clientService.getClientByUsername(payload.get("username").toString());
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping()
  @ResponseBody
  public ResponseEntity<Response> createClient(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clientService.newClient(cliente);
      mailSenderService.sendNewClient(cs.getEmail(), cs.getUsername());
      Response response = new Response(HttpStatus.CREATED, HttpStatus.CREATED.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/carrito")
  @ResponseBody
  public ResponseEntity<Response> addToCart(HttpServletRequest request, @RequestBody Map<String, Integer> formData) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      Map<String, Object> payload = TokensUtils.getPayloadFromToken(
        authorizationHeader.replace("Bearer ", "")
      );
      assert payload != null;
      ClientModel cs = this.clientService.addToCart(formData, payload.get("username").toString());
      Response response = new Response(HttpStatus.CREATED, HttpStatus.CREATED.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/carrito/decrease/{productId}")
  @ResponseBody
  public ResponseEntity<Response> decreaseProduct(HttpServletRequest request, @PathVariable Long productId) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      Map<String, Object> payload = TokensUtils.getPayloadFromToken(
        authorizationHeader.replace("Bearer ", "")
      );
      assert payload != null;
      ClientModel cs = this.clientService.decreaseProduct(productId, payload.get("username").toString());
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/carrito/{productId}")
  @ResponseBody
  public ResponseEntity<Response> deleteProductOnCart(HttpServletRequest request, @PathVariable Long productId) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      Map<String, Object> payload = TokensUtils.getPayloadFromToken(
        authorizationHeader.replace("Bearer ", "")
      );
      assert payload != null;
      boolean cs = this.clientService.deleteProductOnCart(productId, payload.get("username").toString());
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping()
  @ResponseBody
  public ResponseEntity<Response> updateClient(
     HttpServletRequest request,
     @RequestPart(value = "img", required = false) MultipartFile img,
     @RequestParam Map<String, Object> formData
  ) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      Map<String, Object> payload = TokensUtils.getPayloadFromToken(
        authorizationHeader.replace("Bearer ", "")
      );

      assert payload != null;
      boolean pwdChanged = formData.containsKey("new-password");

      ClientModel client = this.formDataToClientModel(formData);

      if (img != null) {
        client.setImg(img.getBytes());
      }

      ClientModel cs = this.clientService.updateClient(client, payload.get("username").toString());

      if(pwdChanged){
        mailSenderService.sendPwdChanged(cs.getEmail());
      }

      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  private ClientModel formDataToClientModel(Map<String, Object> formData) {
    ObjectMapper objectMapper = new ObjectMapper();
    ClientModel client = objectMapper.convertValue(formData, ClientModel.class);
    if(formData.containsKey("new-password")){
      client.setNewPassword(formData.get("new-password").toString());
    }
    return client;
  }

  @DeleteMapping()
  @ResponseBody
  public ResponseEntity<Response> deleteClient(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clientService.deleteClient(cliente);
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getClients() {
    Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), this.clientService.getAllClients());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/pedidos")
  public ResponseEntity<Response> getOrders(HttpServletRequest request) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      Map<String, Object> payload = TokensUtils.getPayloadFromToken(
        authorizationHeader.replace("Bearer ", "")
      );
      assert payload != null;
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), this.clientService.getOrders(Long.parseLong(payload.get("userId").toString())));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

}
