package com.mmanchola.blog.config.security;

import com.mmanchola.blog.auth.ApplicationUserService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

  private ApplicationUserService userDetailsService;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public ApplicationSecurityConfig(
      ApplicationUserService userDetailsService,
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
        // Order DOES matter
//        .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(POST_WRITE.getPermission())
//        .antMatchers(HttpMethod.POST, "/api/**").hasAuthority(POST_WRITE.getPermission())
//        .antMatchers(HttpMethod.PUT, "/api/**").hasAuthority(POST_WRITE.getPermission())
//        .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole(ADMIN.name())
        .anyRequest().authenticated()
        .and()
//        .httpBasic();
      .formLogin()
        .loginPage("/login")
        .permitAll()
        .defaultSuccessUrl("/home", true)
        .usernameParameter("username")    // Explicit declaration. Default: username
        .passwordParameter("password")    // Explicit declaration. Default: password
        .and()
      .rememberMe()   // Default: 2 months
//        .tokenRepository()    // Retrieved from database
        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(120))
        .key("r3$34rch")
        .rememberMeParameter("remember-me")    // Explicit declaration. Default: remember-me
        .and()
      .logout()
        .logoutUrl("/logout")    // Explicit declaration. Default: /logout
        // Change to POST method if csrf is enabled (i.e., POST is better practice for csrf)
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
        .clearAuthentication(true)
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID", "remember-me")
        .logoutSuccessUrl("/home");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
  }
}
