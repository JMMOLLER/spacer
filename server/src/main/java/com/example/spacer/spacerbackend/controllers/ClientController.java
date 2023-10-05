package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.auth.TokensUtils;
import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.services.ClientService;
import com.example.spacer.spacerbackend.services.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/cliente")
public class ClientController {
  ClientService clientService;

  @Autowired
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
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
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), new ClientService().getClientInfoSummary(cs));
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
      Response response = new Response(HttpStatus.CREATED, HttpStatus.CREATED.name(), cs);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
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

      ClientModel client = this.getClientFromFormData(formData);

      if (img != null) {
        String contentType = img.getContentType();
        if(contentType != null && contentType.startsWith("image")) {
          client.setImg(img.getBytes());
        }else{
          throw new Exception("img format not valid");
        }
      }

      ClientModel cs = this.clientService.updateClient(client, payload.get("username").toString(), request);
      Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), new ClientService().getClientInfoSummary(cs));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  private ClientModel getClientFromFormData(Map<String, Object> formData) {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.convertValue(formData, ClientModel.class);
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

}
