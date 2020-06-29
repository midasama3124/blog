package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.dao.PersonRoleDataAccessService;
import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.model.Person;
import com.mmanchola.blog.security.ApplicationUserRole;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private PersonDataAccessService personDas;
  private RoleDataAccessService roleDas;
  private PersonRoleDataAccessService personRoleDas;

  @Autowired
  public UserService(PersonDataAccessService personDas,
      RoleDataAccessService roleDas,
      PersonRoleDataAccessService personRoleDas) {
    this.personDas = personDas;
    this.roleDas = roleDas;
    this.personRoleDas = personRoleDas;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    Person person = personDas.findByEmail(s).orElse(null);

//    // Roles
//    List<GrantedAuthority> roles = new ArrayList<>();
//    roles.add(new SimpleGrantedAuthority(ADMIN.name()));
//    roles.add(new SimpleGrantedAuthority(READER.name()));

    // Authorities / Permissions
    List<Short> roleIds = personRoleDas.find(person.getId());
    Set<SimpleGrantedAuthority> permissions = roleIds
        .stream()
        .map(roleId ->
            ApplicationUserRole
                .valueOf(roleDas.findNameById(roleId))
                .getGrantedAuthorities()
        )
        .flatMap(Set::stream)           // Concatenate into a single set
        .collect(Collectors.toSet());

    UserDetails userDetails = User.builder()
        .username(person.getEmail())
        .password(person.getPasswordHash())
//        .roles(roles)
        .authorities(permissions)
        .build();

    return userDetails;
  }
}
