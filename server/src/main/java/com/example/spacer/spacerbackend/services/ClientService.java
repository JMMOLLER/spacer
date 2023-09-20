package com.example.spacer.spacerbackend.services;

import java.util.ArrayList;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.repositories.ClientRepository;

@Service
public class ClientService {
  @Autowired
  ClientRepository clientRepository;

  public ArrayList<ClientModel> getAllClients() {
    return (ArrayList<ClientModel>) clientRepository.findAll();
  }


  public ClientModel newClient(ClientModel cliente) {
    if(cliente.getId() != null) {
      cliente.setId(null);
    }
    return clientRepository.save(cliente);
  }

  public ClientModel updateClient(ClientModel cliente) {
    if(cliente.getId() != null) {
      Optional<ClientModel> existingClient = clientRepository.findById(cliente.getId());
      if (existingClient.isPresent()) {
        return clientRepository.save(cliente);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede procesar la petición sin el id del cliente.");
  }

  public ClientModel deleteClient(ClientModel cliente) {
    if(cliente.getId() != null) {
      Optional<ClientModel> existingClient = clientRepository.findById(cliente.getId());
      if (existingClient.isPresent()) {
        clientRepository.delete(cliente);
        return cliente;
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede procesar la petición sin el id del cliente.");
  }

}
