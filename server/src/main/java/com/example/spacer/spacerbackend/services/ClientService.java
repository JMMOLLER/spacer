package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {
  ClientRepository clientRepository;
  CartService cartService;

  @Autowired
  public void ClientRepository(ClientRepository clientRepository, CartService cartService) {
    this.clientRepository = clientRepository;
    this.cartService = cartService;
  }

  public ClientModel[] getAllClients() {
    List<ClientModel> clients = clientRepository.findAll();
    return clients.toArray(new ClientModel[0]);
  }

  public ClientModel newClient(ClientModel client) {
    if (client.getId() != null) {
      client.setId(null);
    }
    client.setPassword(new BCryptPasswordEncoder().encode(client.getPassword()));
    client.setRol(0);
    return clientRepository.save(client);
  }

  public ClientModel updateClient(ClientModel client, String username) {
    try {
      if (username != null) {
        Optional<ClientModel> existingClient = clientRepository.findOneByUsername(username);
        if (existingClient.isPresent()) {

          ClientModel newClientData = ClientDataSetter(client, existingClient.get());

          return clientRepository.save(newClientData);
        } else {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
        }
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede procesar la petición sin el username del cliente.");
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  private ClientModel ClientDataSetter(@NonNull ClientModel newClientData, ClientModel currentClientData){

    if (newClientData.getFirstName() != null) {
      currentClientData.setFirstName(newClientData.getFirstName());
    }
    if (newClientData.getLastName() != null) {
      currentClientData.setLastName(newClientData.getLastName());
    }
    if (newClientData.getAddress() != null) {
      currentClientData.setAddress(newClientData.getAddress());
    }
    if (newClientData.getCardNumber() != null) {
      currentClientData.setCardNumber(newClientData.getCardNumber());
    }
    if (newClientData.getEmail() != null) {
      currentClientData.setEmail(newClientData.getEmail());
    }
    if (newClientData.getImg() != null) {
      currentClientData.setImg(newClientData.getImg());
    }
    if (newClientData.getNewPassword() != null) {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      if (encoder.matches(newClientData.getNewPassword(), currentClientData.getPassword())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la anterior.");
      } else if (newClientData.getNewPassword().trim().length() < 6) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe tener al menos 6 caracteres.");
      } else if (!newClientData.getPassword().equals(newClientData.getNewPassword())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden.");
      }else {
        currentClientData.setPassword(encoder.encode(newClientData.getNewPassword()));
      }
    }

    return currentClientData;
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

  public ClientModel getClientByUsername(String username) {
    Optional<ClientModel> client = clientRepository.findOneByUsername(username);
    if (client.isPresent()) {
      return client.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente con el username '{username}' no existe");
    }
  }

  public ClientModel addToCart(Map<String, Integer> formData, String username) {
    try {
      Optional<ClientModel> existingClient = clientRepository.findOneByUsername(username);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        cartService.createOrUpdate(
          Long.valueOf(
            formData.getOrDefault("productId", null)
          ),
          client.getId(),
          formData.getOrDefault("quantity", null)
        );
        return clientRepository.save(client);
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
