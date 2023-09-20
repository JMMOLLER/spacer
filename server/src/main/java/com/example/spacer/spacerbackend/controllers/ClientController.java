package com.example.spacer.spacerbackend.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.services.ClientService;
import com.example.spacer.spacerbackend.services.Response;

import java.util.ArrayList;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/cliente")
public class ClientController {
  @Autowired
  ClientService clienteService;

  @GetMapping()
  public ArrayList<ClientModel> obtenerClientes() {
    return clienteService.getAllClients();
  }

  @PostMapping()
  @ResponseBody
  public ResponseEntity<?> crearCliente(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clienteService.newClient(cliente);
      Response response = new Response(HttpStatus.CREATED, "Petición exitosa", cs);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping()
  @ResponseBody
  public ResponseEntity<?> actualizarCliente(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clienteService.updateClient(cliente);
      Response response = new Response(HttpStatus.OK, "Actualización exitosa", cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping()
  @ResponseBody
  public ResponseEntity<?> eliminarCliente(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clienteService.deleteClient(cliente);
      Response response = new Response(HttpStatus.OK, "Eliminación exitosa", cs);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      Response response = new Response(HttpStatus.BAD_REQUEST, ExceptionUtils.getRootCause(e).getMessage(), null);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

}
