package com.example.spacer.spacerbackend.auth;

import com.example.spacer.spacerbackend.models.ClientModel;
import com.example.spacer.spacerbackend.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailsServices implements UserDetailsService {

  private final ClientRepository clientRepository;

  @Autowired
  public ClientDetailsServices(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    ClientModel client = clientRepository.findOneClientModelByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new ClientDetailsImp(client);
  }
}
