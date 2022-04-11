package com.junive.account.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.junive.account.service.TokenService;
import com.junive.account.util.CustomStatus;
import com.junive.account.util.CustomURL;
import com.junive.account.util.ServletUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal( HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
                                    throws ServletException, IOException {

        if (request.getServletPath().equals(CustomURL.loginUrl)
                || request.getServletPath().equals(CustomURL.tokenRefreshUrl)) {
            filterChain.doFilter(request, response); // pass the request to the next filter
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith(CustomURL.tokenStartName)) {
                try {
                    log.info("Adding the Authorizations to user");
                    String token = tokenService.getTokenByHeader(authorizationHeader);

                    String username = tokenService.getUsernameByToken(token);
                    String[] roles = tokenService.getRolesByToken(token);

                    if (roles == null) {
                        // Filter doesn't trigger @ExceptionHandler when throw exception
                        ServletUtil.errorResponse(request, response,
                                HttpStatus.NOT_FOUND, CustomStatus.ROLE_NOT_FOUND);
                        throw new RoleNotFoundException("Authorization filter : Role not found");
                    }
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> { // Convert string role in authorities
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                    log.info("Finish the Authorizations to user");
                } catch(TokenExpiredException ex) {
                    log.error("Token has Expired", ex.getMessage());
                    ServletUtil.errorResponse(request, response,
                            HttpStatus.GONE, CustomStatus.TOKEN_EXPIRED);
                } catch (Exception exception) {
                    log.error("Error logging in: {}", exception.getMessage());
                    ServletUtil.errorResponse(request, response,
                            HttpStatus.NOT_ACCEPTABLE,
                            CustomStatus.NOT_VALID_TOKEN,
                            exception.getMessage());
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
