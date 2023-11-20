package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.InvoiceModel;
import com.example.spacer.spacerbackend.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
  InvoiceRepository invoiceRepository;

  @Autowired
  public void InvoiceRepository(InvoiceRepository invoiceRepository) {
    this.invoiceRepository = invoiceRepository;
  }

  public InvoiceModel[] getAllInvoices() {
    return invoiceRepository.findAll().toArray(new InvoiceModel[0]);
  }

//  @Cacheable(value = "invoices", key = "#clientId")
  public InvoiceModel[] getInvoicesByClientId(Long clientId) {
    return invoiceRepository.findByClientId(clientId).toArray(new InvoiceModel[0]);
  }
}
