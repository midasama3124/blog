package com.mmanchola.blog.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService implements UserDetailsService {

  private final ApplicationUserDao applicationUserDao;

  public ApplicationUserService(ApplicationUserDao applicationUserDao) {
    this.applicationUserDao = applicationUserDao;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    /* First approach without implementing UserDetails interface */
//    // Roles (Dummy list)
//    List<GrantedAuthority> roles = new ArrayList<>();
//    roles.add(new SimpleGrantedAuthority(ADMIN.name()));
//    roles.add(new SimpleGrantedAuthority(READER.name()));
//    UserDetails userDetails = User.builder()
//    .username(person.getEmail())
//    .password(person.getPasswordHash())
////        .roles(roles)
//    .authorities(permissions)
//    .build();
//    return userDetails;

    return applicationUserDao
        .selectApplicationUserByUsername(username)
        .orElseThrow(() ->
            new UsernameNotFoundException(String.format("Username %s not found", username)));
  }
}
