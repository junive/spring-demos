package com.junive.account.security;

import com.junive.account.service.TokenService;
import com.junive.account.util.CustomStatus;
import com.junive.account.util.CustomText;
import com.junive.account.util.ServletUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final TokenService tokenService;

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
                                                throws AuthenticationException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username is: {}, Password is: {}", username, password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                           AuthenticationException exception) {

        String username = request.getParameter("username");
        ServletUtil.errorResponse(request, response,
                HttpStatus.NOT_FOUND,
                CustomStatus.USERNAME_PASSWORD_NOT_FOUND,
                username);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authentication) {

        User user = (User) authentication.getPrincipal(); // user successfully authenticated

        String accessToken = tokenService.createAccessToken(
                user.getUsername(), ServletUtil.getUrlByRequest(request),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );

        String refreshToken = tokenService.createRefreshToken(
                user.getUsername(), ServletUtil.getUrlByRequest(request)
        );

        Map<String, Object> tokens = new HashMap<>();
        tokens.put(CustomText.tokenAccessName, accessToken);
        tokens.put(CustomText.tokenRefreshName, refreshToken);
        tokens.put(CustomText.userResponse, user);
        response.setContentType(APPLICATION_JSON_VALUE);
        ServletUtil.okResponse(response, tokens);

    }
}
