package com.example.spacer.spacerbackend.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.spacer.spacerbackend.models.ClientModel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientDetailsImp implements UserDetails {


  private final ClientModel ClientModel;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public String getPassword() {
    return ClientModel.getPassword();
  }

  @Override
  public String getUsername() {
    return ClientModel.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getEmail(){
    return ClientModel.getEmail();
  }
  
}
