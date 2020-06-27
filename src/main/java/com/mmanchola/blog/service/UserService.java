package com.mmanchola.blog.service;

import static com.mmanchola.blog.security.ApplicationUserRole.ADMIN;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private PersonDataAccessService dataAccessService;

  @Autowired
  public UserService(PersonDataAccessService dataAccessService) {
    this.dataAccessService = dataAccessService;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    Person person = dataAccessService.findByEmail(s).orElse(null);

    // TODO: Retrieve corresponding roles from database
//    List<GrantedAuthority> roles = new ArrayList<>();
//    roles.add(new SimpleGrantedAuthority(ADMIN.name()));
//    roles.add(new SimpleGrantedAuthority(READER.name()));

    UserDetails userDetails = User.builder()
        .username(person.getEmail())
        .password(person.getPasswordHash())
        .authorities(ADMIN.getGrantedAuthorities())
        .build();

    return userDetails;
  }
}
