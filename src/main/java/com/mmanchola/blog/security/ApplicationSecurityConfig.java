package com.mmanchola.blog.security;

import static com.mmanchola.blog.security.ApplicationUserRole.ADMIN;
import static com.mmanchola.blog.security.ApplicationUserRole.READER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public ApplicationSecurityConfig(
      PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        .antMatchers("/", "index", "/css/*", "/js/*")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .httpBasic();
  }

  @Override
  @Bean
  protected UserDetailsService userDetailsService() {
    UserDetails miguelmanchola = User.builder()
        .username("miguelmanchola")
        .password(passwordEncoder.encode("cabj5610"))
        .roles(ADMIN.name())
        .build();

    UserDetails angelasanchez = User.builder()
        .username("angelasanchez")
        .password(passwordEncoder.encode("contraseña"))
        .roles(READER.name())
        .build();

    return new InMemoryUserDetailsManager(
        miguelmanchola,
        angelasanchez
    );
  }
}
