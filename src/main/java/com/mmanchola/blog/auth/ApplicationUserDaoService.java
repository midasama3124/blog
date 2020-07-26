package com.mmanchola.blog.auth;

import com.mmanchola.blog.dao.PersonDataAccessService;
import com.mmanchola.blog.dao.PersonRoleDataAccessService;
import com.mmanchola.blog.dao.RoleDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Person;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mmanchola.blog.config.security.ApplicationUserRole.valueOf;
import static com.mmanchola.blog.exception.ExceptionMessage.NOT_FOUND;
import static com.mmanchola.blog.model.TableFields.PERSON_EMAIL;

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
        Person person = personDas.find(username)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(PERSON_EMAIL.toString())));

        // Authorities / Permissions
        List<Short> roleIds = personRoleDas.findRoles(person.getId());
        Set<SimpleGrantedAuthority> permissions = roleIds
                .stream()
                .map(roleId ->
                        valueOf(roleDas.findNameById(roleId).get())
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
