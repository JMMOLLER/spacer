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
  
  @Autowired
  private ClientRepository clienteRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    ClientModel cliente = clienteRepository.findOneClientModelByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    return new ClientDetailsImp(cliente);
  }
}
