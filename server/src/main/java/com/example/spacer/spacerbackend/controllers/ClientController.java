package com.example.spacer.spacerbackend.controllers;

import com.example.spacer.spacerbackend.models.CardModel;
import com.example.spacer.spacerbackend.models.CartModel;
import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.InvoiceModel;
import com.example.spacer.spacerbackend.services.ClientService;
import com.example.spacer.spacerbackend.services.MailSenderService;
import com.example.spacer.spacerbackend.services.Response;
import com.example.spacer.spacerbackend.utils.CustomException;
import com.example.spacer.spacerbackend.utils.UserCredential;
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
      UserCredential userCredential = new UserCredential(request);

      ClientModel cs = this.clientService.getClientByUsername(userCredential.getUsername());

      return new Response(HttpStatus.OK.name(), cs).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @PostMapping()
  @ResponseBody
  public ResponseEntity<Response> createClient(@RequestBody ClientModel cliente) {
    try {
      ClientModel cs = this.clientService.newClient(cliente);

      mailSenderService.sendNewClient(cs.getEmail(), cs.getUsername());

      return new Response(HttpStatus.CREATED.name(), cs).createdResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @GetMapping("/carrito")
  @ResponseBody
  public ResponseEntity<Response> getClientCart(HttpServletRequest request) {
    try {
      UserCredential userCredential = new UserCredential(request);

      CartModel[] cart = this.clientService.getCart(userCredential.getUsername());

      return new Response(HttpStatus.OK.name(), cart).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @PostMapping("/carrito")
  @ResponseBody
  public ResponseEntity<Response> addToCart(HttpServletRequest request, @RequestBody Map<String, Integer> formData) {
    try {
      UserCredential userCredential = new UserCredential(request);

      CartModel[] cart = this.clientService.addToCart(formData, userCredential.getUsername());

      return new Response(HttpStatus.OK.name(), cart).createdResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @PostMapping("/carrito/decrease/{productId}")
  @ResponseBody
  public ResponseEntity<Response> decreaseProduct(HttpServletRequest request, @PathVariable Long productId) {
    try {
      UserCredential userCredential = new UserCredential(request);

      ClientModel client = this.clientService.decreaseProduct(productId, userCredential.getUsername());

      return new Response(HttpStatus.OK.name(), client).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @DeleteMapping("/carrito/{productId}")
  @ResponseBody
  public ResponseEntity<Response> deleteProductOnCart(HttpServletRequest request, @PathVariable Long productId) {
    try {
      UserCredential userCredential = new UserCredential(request);

      boolean cs = this.clientService.deleteProductOnCart(productId, userCredential.getUsername());

      return new Response(HttpStatus.OK.name(), cs).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
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
      UserCredential userCredential = new UserCredential(request);

      boolean pwdChanged = formData.containsKey("new-password");

      ClientModel client = this.formDataToClientModel(formData);

      if (img != null) {
        client.setImg(img.getBytes());
      }

      ClientModel cs = this.clientService.updateClient(client, userCredential.getUsername());

      if(pwdChanged){
        mailSenderService.sendPwdChanged(cs.getEmail());
      }

      return new Response(HttpStatus.OK.name(), cs).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
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

      return new Response(HttpStatus.OK.name(), cs).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @GetMapping("/all")
  public ResponseEntity<Response> getClients() {
    try {
      ClientModel[] clients = this.clientService.getAllClients();
      return new Response(HttpStatus.OK.name(), clients).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @PostMapping("/carrito/pagar")
  public ResponseEntity<Response> purchase(HttpServletRequest request, @RequestBody CardModel cardInfo) {
    try {
      UserCredential userCredential = new UserCredential(request);

      ClientModel client = this.clientService.getClientByUsername(userCredential.getUsername());

      if(cardInfo == null && client.getCardId() == null){
        return new Response("No existe un método de pago vinculado a esta cuenta").customResponse(HttpStatus.PAYMENT_REQUIRED);
      } else if (cardInfo != null && cardInfo.getCardNumber() != null) {
        String res = this.clientService.updateCardForClient(client, cardInfo);
        if(res != null){
          return new Response(res).badRequestResponse();
        }
      } else {
        assert cardInfo != null;
        boolean res = this.clientService.validateCodeCard(client.getId(), cardInfo.getCvv());
        if(!res) return new Response("El código de seguridad de la tarjeta es incorrecto").unauthorizedResponse();
      }

      InvoiceModel invoice = this.clientService.purchase(userCredential.getUserId());
      return new Response(HttpStatus.OK.name(), invoice).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

  @GetMapping("/pedidos")
  public ResponseEntity<Response> getOrders(HttpServletRequest request) {
    try {
      UserCredential userCredential = new UserCredential(request);

      InvoiceModel[] invoices = this.clientService.getOrders(userCredential.getUserId());

      return new Response(HttpStatus.OK.name(), invoices).okResponse();
    } catch (CustomException e) {
      return new Response(e.getMessage()).customResponse(e.getStatus());
    } catch (Exception e) {
      return new Response(ExceptionUtils.getRootCause(e).getMessage()).internalServerErrorResponse();
    }
  }

}
