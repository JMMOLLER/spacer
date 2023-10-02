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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {
  ClientRepository clientRepository;

  @Autowired
  public void ClientRepository(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public ArrayList<Object> getAllClients() {
    ArrayList<ClientModel> clients = (ArrayList<ClientModel>) clientRepository.findAll();
    ArrayList<Object> clientsSummary = new ArrayList<>();
    for (ClientModel client : clients) {
      try {
        clientsSummary.add(new ClientService().getClientInfoSummary(client));
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return clientsSummary;
  }

  public ClientModel newClient(ClientModel client) {
    if (client.getId() != null) {
      client.setId(null);
    }
    if(client.getUrl_img() == null){
      client.setUrl_img("https://spacer-ecommerce.vercel.app/assets/imgs/user.webp");
    }
    client.setPassword(new BCryptPasswordEncoder().encode(client.getPassword()));
    client.setRol(0);
    return clientRepository.save(client);
  }

  public ClientModel updateClient(ClientModel client, String username, HttpServletRequest request) {
    try {
      if (username != null) {
        Optional<ClientModel> existingClient = clientRepository.findOneByUsername(username);
        if (existingClient.isPresent()) {

          ClientModel newClientData = ClientDataSetter(client, existingClient.get());

          if(client.getImg() != null){
            String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
            newClientData.setUrl_img(baseUrl + "/cliente/" + newClientData.getUsername() + ".jpg");
          }

          return clientRepository.save(newClientData);
        } else {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
        }
      }
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    return client;
  }

  private ClientModel ClientDataSetter(@NonNull ClientModel newClientData, ClientModel currentClientData){

    if (newClientData.getFirstName() != null) {
      currentClientData.setFirstName(newClientData.getFirstName());
    }
    if (newClientData.getLastName() != null) {
      currentClientData.setLastName(newClientData.getLastName());
    }
    if (newClientData.getPassword() != null) {
      currentClientData.setPassword(newClientData.getPassword());
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

  public Map<String, Object> getClientInfoSummary(ClientModel cs) throws IllegalAccessException {
    Map<String, Object> client = new HashMap<>();

    Field[] fields = cs.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      if(field.getName().equals("img")) continue;
      String fieldName = field.getName();
      Object value = field.get(cs);
      client.put(fieldName, value);
    }

    return client;
  }
}
