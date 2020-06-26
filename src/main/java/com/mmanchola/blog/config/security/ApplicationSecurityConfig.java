package com.mmanchola.blog.security;

import static com.mmanchola.blog.security.ApplicationUserRole.ADMIN;

import com.mmanchola.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

  private UserService userDetailsService;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public ApplicationSecurityConfig(
      UserService userDetailsService,
      PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .authorizeRequests()
        .antMatchers("/", "/home", "/dist/**", "/register", "/login*").permitAll()
        .antMatchers("/api/**").hasRole(ADMIN.name())
        .anyRequest().authenticated()
        .and()
//        .httpBasic();
      .formLogin()
        .loginPage("/login")
        .permitAll()
        .and()
      .logout()
        .logoutSuccessUrl("/home")
        .permitAll();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
  }
}
