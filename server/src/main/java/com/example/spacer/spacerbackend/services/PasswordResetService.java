package com.example.spacer.spacerbackend.services;

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

  public PasswordResetModel getPrByCode(String code, Long clientId) {
    PasswordResetModel pr = this.passwordResetRepository.findByCode(code, clientId).orElse(null);
    if (pr == null) return null;
    else {
      if (pr.getId().substring(0, 5).equals(code)) return pr;
      else return null;
    }
  }

  public void deleteRequestReset(String id) {
    this.passwordResetRepository.deleteById(id);
  }
}
