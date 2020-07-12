package com.mmanchola.blog.auth;

import com.mmanchola.blog.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private PersonService personService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        ApplicationUser authUser = (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        session.setAttribute("user", authUser);
        session.setAttribute("username", authUser.getUsername());
        session.setAttribute("authorities", authentication.getAuthorities());

        // Update last login in database
        personService.updateLastLogin(authUser.getUsername());

        // Set our response to OK status
        response.setStatus(HttpServletResponse.SC_OK);

        // Redirect to previously requested URL if any, otherwise go home
        String requestUrl = Optional.ofNullable(new HttpSessionRequestCache().getRequest(request, response))
                .map(SavedRequest::getRedirectUrl)   // Set redirection URL if previously requested
                .orElse("/");   // Go home by default
        response.sendRedirect(requestUrl);
    }
}
