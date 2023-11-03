package com.roma42.eventifyBack.security;

import com.roma42.eventifyBack.services.UserCredentialService;
import com.roma42.eventifyBack.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class UserEnableFilter extends OncePerRequestFilter {
    private final String[] publicApis = {
            "/api/v1/event/all",
            "/api/v1/credential/signUp",
            "/api/v1/credential/login",
            "/api/v1/credential/upload",
            "/api/v1/credential/verify",
            "/api/v1/credential/refresh",
            "/api/v1/event/image"
    };
    private final UserCredentialService userCredentialService;
    private final static Logger log = LoggerFactory.getLogger(UserEnableFilter.class);

    public UserEnableFilter(UserCredentialService userCredentialService) {
        this.userCredentialService = userCredentialService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (Arrays.stream(publicApis).toList().contains(request.getServletPath())) {
            log.debug("public API detected");
            filterChain.doFilter(request, response);
            return ;
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null) {
            User user = this.userCredentialService.findUserCredentialByUsername(username).getUser();
            if (user != null && !user.isBlocked() && !user.isLoggedOut()) {
                log.debug("request valid");
                filterChain.doFilter(request, response);
                return;
            }
            else {
                if (user == null)
                    log.debug("user is null");
                else
                    log.debug("user blocked or logged out");
            }
        }
        else
            log.debug("username is null");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write("You may be blocked or not logged");
    }
}