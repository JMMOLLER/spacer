package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.services.ClientService;
import com.example.spacer.spacerbackend.services.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
public class ClientController {
  ClientService clientService;

  @Autowired
  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @GetMapping()
  public ResponseEntity<Response> getClients() {
    Response response = new Response(HttpStatus.OK, HttpStatus.OK.name(), this.clientService.getAllClients());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping()
  @ResponseBody
  public ResponseEntity<Response> createClient(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clientService.newClient(cliente);
      Response response = new Response(HttpStatus.CREATED, "Petición exitosa", cs);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping()
  @ResponseBody
  public ResponseEntity<Response> updateClient(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clientService.updateClient(cliente);
      Response response = new Response(HttpStatus.OK, "Actualización exitosa", cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping()
  @ResponseBody
  public ResponseEntity<Response> deleteClient(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clientService.deleteClient(cliente);
      Response response = new Response(HttpStatus.OK, "Eliminación exitosa", cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

}
