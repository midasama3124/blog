package com.mmanchola.blog.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthSuccessHandlerConfig {

    @Bean
    public AuthSuccessHandler authSuccessHandler() {
        return new AuthSuccessHandler();
    }
}
