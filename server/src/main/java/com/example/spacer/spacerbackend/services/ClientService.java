package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.CardModel;
import com.example.spacer.spacerbackend.models.CartModel;
import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.InvoiceModel;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import com.example.spacer.spacerbackend.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {
  ClientRepository clientRepository;
  CartService cartService;
  InvoiceService invoiceService;
  CardService cardService;

  @Autowired
  public void ClientRepository(ClientRepository clientRepository, CartService cartService, InvoiceService invoiceService, CardService cardService) {
    this.clientRepository = clientRepository;
    this.invoiceService = invoiceService;
    this.cartService = cartService;
    this.cardService = cardService;
  }

  @Cacheable(value = "clients")
  public ClientModel[] getAllClients() {
    List<ClientModel> clients = clientRepository.findAll();
    return clients.toArray(new ClientModel[0]);
  }

  @CacheEvict(value = "clients", allEntries = true)
  public ClientModel newClient(ClientModel client) {
    if (client.getId() != null) {
      client.setId(null);
    }
    client.setPassword(new BCryptPasswordEncoder().encode(client.getPassword()));
    client.setRol(0);
    return clientRepository.save(client);
  }

  @CacheEvict(value = "client", key = "#client.username")
  public ClientModel updateClient(ClientModel client) {
    try {
      Optional<ClientModel> currentClient = clientRepository.findOneByUsername(client.getUsername());
      if (currentClient.isPresent()) {

        ClientModel newClientData = ClientDataSetter(client, currentClient.get());

        return clientRepository.save(newClientData);
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente con el username '"+ client.getUsername() +"' no existe");
      }
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Cacheable(value = "client", key = "#username")
  public ClientModel getClientByUsername(String username) {
    try{
      Optional<ClientModel> client = clientRepository.findOneByUsername(username);
      if (client.isPresent()) {
        return client.get();
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente con el username '"+ username +"' no existe");
      }
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "client", key = "#client.username")
  public ClientModel updatePassword(ClientModel client, String newPassword) {
    try {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
      if (newPassword.trim().length() < 6) {
        throw new CustomException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe tener al menos 6 caracteres.");
      } else if (encoder.matches(newPassword, client.getPassword())) {
        throw new CustomException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la anterior.");
      }else{
        client.setPassword(encoder.encode(newPassword));
        clientRepository.save(client);
        return client;
      }
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private ClientModel ClientDataSetter(@NonNull ClientModel newClientData, ClientModel currentClientData) {

    if (newClientData.getFirstName() != null && !newClientData.getFirstName().equals(currentClientData.getFirstName())) {
      currentClientData.setFirstName(newClientData.getFirstName());
    }
    if (newClientData.getLastName() != null && !newClientData.getLastName().equals(currentClientData.getLastName())) {
      currentClientData.setLastName(newClientData.getLastName());
    }
    if (newClientData.getAddress() != null && !newClientData.getAddress().equals(currentClientData.getAddress())) {
      currentClientData.setAddress(newClientData.getAddress());
    }
    if (newClientData.getEmail() != null && !newClientData.getEmail().equals(currentClientData.getEmail())) {
      currentClientData.setEmail(newClientData.getEmail());
    }
    if (newClientData.getImg() != null && !Arrays.equals(newClientData.getImg(), currentClientData.getImg())) {
      currentClientData.setImg(newClientData.getImg());
    }
    if (newClientData.getNewPassword() != null) {
      BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

      if(newClientData.getPassword() == null || newClientData.getNewPassword() == null){
        throw new CustomException(HttpStatus.BAD_REQUEST, "Debe enviar su nueva contraseña 2 veces para validar coincidencia.");
      }

      if (encoder.matches(newClientData.getNewPassword(), currentClientData.getPassword())) {
        throw new CustomException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la anterior.");
      } else if (newClientData.getNewPassword().trim().length() < 6) {
        throw new CustomException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe tener al menos 6 caracteres.");
      } else if (!newClientData.getPassword().equals(newClientData.getNewPassword())) {
        throw new CustomException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden.");
      } else {
        currentClientData.setPassword(encoder.encode(newClientData.getNewPassword()));
      }
    }

    return currentClientData;
  }

  @CacheEvict(value = "client", key = "#client.username")
  public void deleteClient(ClientModel client) {
    try{
      clientRepository.delete(client);
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "client", key = "#username")
  public CartModel[] addToCart(Map<String, Integer> formData, String username) {
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
        return this.getCart(username);
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public CartModel[] getCart(String username) {
    try {
      Optional<ClientModel> existingClient = clientRepository.findOneByUsername(username);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        return client.getCart().toArray(new CartModel[0]);
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "client", key = "#username")
  public ClientModel decreaseProduct(Long productId, String username) {
    try {
      Optional<ClientModel> existingClient = clientRepository.findOneByUsername(username);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        cartService.decreaseProduct(
          productId,
          client.getId()
        );
        return clientRepository.save(client);
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "client", key = "#username")
  public boolean deleteProductOnCart(Long productId, String username) {
    try {
      Optional<ClientModel> existingClient = clientRepository.findOneByUsername(username);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        cartService.deleteProduct(
          productId,
          client.getId()
        );
        return true;
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public InvoiceModel[] getOrders(Long clientId) {
    try {
      Optional<ClientModel> existingClient = clientRepository.findById(clientId);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        return invoiceService.getInvoicesByClientId(client.getId());
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  public ClientModel getClientByEmail(String email) {
    Optional<ClientModel> client = clientRepository.findOneByEmail(email);
    return client.orElse(null);
  }

  public InvoiceModel purchase(Long userId) {
    try{
      Optional<ClientModel> existingClient = clientRepository.findById(userId);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        InvoiceModel invoice = invoiceService.createInvoice(client);
        if(cartService.deleteCart(client) == 0) {
          invoiceService.deleteInvoice(invoice.getId());
          throw new CustomException(HttpStatus.BAD_REQUEST, "No existen productos en el carrito");
        }
        return invoice;
      } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @CacheEvict(value = "client", key = "#client.username")
  public String updateCardForClient(ClientModel client, CardModel card) {
    try {
      String error = findErrorCardModel(card);
      if(error != null) return (error);

      CardModel newCard;
      if(client.getCardId() == null){
        newCard = cardService.newCard(card);
      }else{
        if(validateUpdateCardModel(card, client)) {
          card.setId(client.getCardId().getId());
          newCard = cardService.updateCard(card);
        }else{
          return null;
        }
      }
      client.setCardId(newCard);
      clientRepository.save(client);
      return null;
    } catch (CustomException e) {
      throw e;
    }  catch (Exception e) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private boolean validateUpdateCardModel(CardModel card, ClientModel client) {
    if(card.getCardNumber().equals(client.getCardId().getCardNumber())) return false;
    return !card.getCvv().equals(client.getCardId().getCvv());
  }

  private String findErrorCardModel(CardModel card) {
    if(card.getCardNumber() == null || (card.getCardNumber().toString().trim().length() < 13 || card.getCardNumber().toString().trim().length() > 19)) {
      return ("El número de tarjeta no es válido");
    } else if(card.getCardHolder() == null || card.getCardHolder().trim().length() < 3) {
      return ("El nombre del titular no es válido");
    } else if(card.getExpirationDate().isBefore(java.time.LocalDate.now())) {
      return("La fecha de expiración no es válida");
    } else if(card.getCvv() == null || card.getCvv().toString().trim().length() != 3) {
      return("El código de seguridad no es válido");
    } else {
      return null;
    }
  }

  public boolean validateCodeCard(Long clientId, Short cvv) {
    try {
      Optional<ClientModel> existingClient = clientRepository.findById(clientId);
      if (existingClient.isPresent()) {
        ClientModel client = existingClient.get();
        if(client.getCardId() == null){
          throw new CustomException(HttpStatus.BAD_REQUEST, "No existe un método de pago vinculado a esta cuenta");
        }
        return client.getCardId().getCvv().equals(cvv);
      } else {
        throw new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe");
      }
    } catch (CustomException e) {
      throw e;
    } catch (Exception e) {
      throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
