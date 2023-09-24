package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ClientService {
  ClientRepository clientRepository;

  @Autowired
  public void ClientRepository(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public ArrayList<ClientModel> getAllClients() {
    return (ArrayList<ClientModel>) clientRepository.findAll();
  }


  public ClientModel newClient(ClientModel client) {
    if (client.getId() != null) {
      client.setId(null);
    }
    return clientRepository.save(client);
  }

  public ClientModel updateClient(ClientModel client) {
    if (client.getId() != null) {
      Optional<ClientModel> existingClient = clientRepository.findById(client.getId());
      if (existingClient.isPresent()) {
        return clientRepository.save(client);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede procesar la petición sin el id del cliente.");
  }

  public ClientModel deleteClient(ClientModel client) {
    if (client.getId() != null) {
      Optional<ClientModel> existingClient = clientRepository.findById(client.getId());
      if (existingClient.isPresent()) {
        clientRepository.delete(client);
        return client;
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede procesar la petición sin el id del cliente.");
  }

}
