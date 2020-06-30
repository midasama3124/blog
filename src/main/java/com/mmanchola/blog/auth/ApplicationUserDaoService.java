package com.mmanchola.blog.auth;

import static com.mmanchola.blog.security.ApplicationUserRole.valueOf;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.dao.PersonRoleDataAccessService;
import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.model.Person;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationUserDaoService implements ApplicationUserDao {

  private PersonDataAccessService personDas;
  private RoleDataAccessService roleDas;
  private PersonRoleDataAccessService personRoleDas;

  public ApplicationUserDaoService(PersonDataAccessService personDas,
      RoleDataAccessService roleDas,
      PersonRoleDataAccessService personRoleDas) {
    this.personDas = personDas;
    this.roleDas = roleDas;
    this.personRoleDas = personRoleDas;
  }

  @Override
  public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
    Person person = personDas.findByEmail(username).orElse(null);

    // Authorities / Permissions
    List<Short> roleIds = personRoleDas.find(person.getId());
    Set<SimpleGrantedAuthority> permissions = roleIds
        .stream()
        .map(roleId ->
            valueOf(roleDas.findNameById(roleId))
                .getGrantedAuthorities()
        )
        .flatMap(Set::stream)           // Concatenate into a single set
        .collect(Collectors.toSet());

    ApplicationUser applicationUser = new ApplicationUser(
        person.getEmail(),
        person.getPasswordHash(),
        permissions,
        true,
        true,
        true,
        true
    );
    return Optional.ofNullable(applicationUser);
  }
}
