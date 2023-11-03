package com.roma42.eventifyBack.security;

import com.roma42.eventifyBack.dto.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RequestThrottleFilter implements Filter {

    @Value("${requests.max.all}")
    private Integer MAX_REQUESTS_ALL;
    @Value("${requests.max.login}")
    private Integer MAX_REQUESTS_LOGIN;
    private final static String LOGIN_API = "/api/v1/credential/login";
    private final static Logger log = LoggerFactory.getLogger(RequestThrottleFilter.class);
    private final LoadingCache<String, Integer> requestCountsPerIpAddress;
    private final LoadingCache<String, Integer> requestCountsPerLogin;

    public RequestThrottleFilter(){
        super();
        this.requestCountsPerIpAddress = Caffeine.newBuilder()
                .expireAfterWrite(500, TimeUnit.MILLISECONDS)
                .build(key -> 0);
        this.requestCountsPerLogin = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build(key -> 0);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String clientIpAddress = getClientIP((HttpServletRequest) servletRequest);
        log.trace(clientIpAddress);
        if (isMaximumRequestsPerSecondExceeded(this.requestCountsPerIpAddress, clientIpAddress, MAX_REQUESTS_ALL)) {
            log.debug("too many request from [" + clientIpAddress + "]");
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests");
            return;
        }
        String endpoint = ((HttpServletRequest) servletRequest).getServletPath();
        if (endpoint.equals(LOGIN_API)
                && isMaximumRequestsPerSecondExceeded(this.requestCountsPerLogin, clientIpAddress, MAX_REQUESTS_LOGIN)) {
            String body = getBody(servletRequest);
            try {
                LoginDto loginDto = new ObjectMapper().readValue(body, LoginDto.class);
                log.info("too many login tries for [" + loginDto.getUsername() + "] with ip [" + clientIpAddress + "]");
            } catch (UnrecognizedPropertyException ignore) {}
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write("Too many requests");
            return;
        }
        log.debug("request accepted");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isMaximumRequestsPerSecondExceeded(LoadingCache<String, Integer> map,
                                                       String clientIpAddress,
                                                       Integer maxRequests){
        Integer requests = map.get(clientIpAddress);
        if(requests != null) {
            if(requests > maxRequests) {
                map.asMap().remove(clientIpAddress);
                map.put(clientIpAddress, requests);
                return true;
            }
        } else {
            requests = 0;
        }
        requests++;
        map.put(clientIpAddress, requests);
        return false;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    private String getBody(ServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }
}