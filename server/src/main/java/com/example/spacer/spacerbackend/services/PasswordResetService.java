package com.example.spacer.spacerbackend.services;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.models.PasswordResetModel;
import com.example.spacer.spacerbackend.repositories.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

  PasswordResetRepository passwordResetRepository;

  @Autowired
  public void PasswordResetRepository(PasswordResetRepository passwordResetRepository) {
    this.passwordResetRepository = passwordResetRepository;
  }

  public String createRequestReset(Long userId) {
    PasswordResetModel pr = new PasswordResetModel();
    pr.setClientId(userId);
    this.passwordResetRepository.save(pr);
    return pr.getId();
  }

  public PasswordResetModel getPrByClientId(Long clientId) {
    return this.passwordResetRepository.findOneByClientId(clientId).orElse(null);
  }

  public PasswordResetModel getPrById(String id) {
    return this.passwordResetRepository.findById(id).orElse(null);
  }

  public boolean deleteRequestReset(String id) {
    try {
      this.passwordResetRepository.deleteById(id);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
