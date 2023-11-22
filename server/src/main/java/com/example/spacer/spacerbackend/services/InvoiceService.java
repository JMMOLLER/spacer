package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.InvoiceModel;
import com.example.spacer.spacerbackend.models.ProductInvoiceModel;
import com.example.spacer.spacerbackend.models.ProductModel;
import com.example.spacer.spacerbackend.repositories.InvoiceRepository;
import com.example.spacer.spacerbackend.repositories.ProductInvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
  InvoiceRepository invoiceRepository;
  ProductInvoiceRepository productInvoiceRepository;

  @Autowired
  public void InvoiceRepository(InvoiceRepository invoiceRepository, ProductInvoiceRepository productInvoiceRepository) {
    this.invoiceRepository = invoiceRepository;
    this.productInvoiceRepository = productInvoiceRepository;
  }

  public InvoiceModel[] getAllInvoices() {
    return invoiceRepository.findAll().toArray(new InvoiceModel[0]);
  }

//  @Cacheable(value = "invoices", key = "#clientId")
  public InvoiceModel[] getInvoicesByClientId(Long clientId) {
    return invoiceRepository.findByClientId(clientId).toArray(new InvoiceModel[0]);
  }

  void deleteInvoice(Long invoiceId) {
    invoiceRepository.deleteById(invoiceId);
  }

  public InvoiceModel createInvoice(ClientModel client) {
    InvoiceModel invoice = new InvoiceModel();
    invoice.setClientId(client.getId());
    invoice.setTotal(0.0);
    invoiceRepository.save(invoice);
    double[] total = {0.0};
    client.getCart().forEach(product -> {
      addProductToInvoice(product.getProductId(), invoice, product.getQuantity());
      total[0] += product.getProductId().getPrice() * product.getQuantity();
    });
    invoice.setTotal(total[0]);
    return invoiceRepository.save(invoice);
  }

  public void addProductToInvoice(ProductModel product, InvoiceModel invoice, Integer quantity) {
    var productInvoice = new ProductInvoiceModel();
    productInvoice.setInvoiceId(invoice);
    productInvoice.setProductId(product);
    productInvoice.setQuantity(quantity);
    productInvoiceRepository.save(productInvoice);
  }
}
