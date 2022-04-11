package com.junive.account.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.junive.account.model.AppUser;
import com.junive.account.model.Role;
import com.junive.account.model.RoleRequest;
import com.junive.account.service.TokenService;
import com.junive.account.service.UserService;
import com.junive.account.util.CustomStatus;
import com.junive.account.util.CustomURL;
import com.junive.account.util.ServletUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;


    @GetMapping(CustomURL.allUserUrl)
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping(CustomURL.saveUserUrl)
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI url = ServletUtil.getUriByPath(CustomURL.saveUserUrl);
        return ResponseEntity.created(url).body(userService.saveUser(user));
    }

    @PostMapping(CustomURL.saveRoleUrl)
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI url  = ServletUtil.getUriByPath( CustomURL.saveRoleUrl);
        return ResponseEntity.created(url).body(userService.saveRole(role));
    }

    @PostMapping( CustomURL.addRoleUrl)
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleRequest request) {
        userService.addRoleToUser(request.username(), request.roleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(CustomURL.findUserUrl+ "/{username}")
    public ResponseEntity<AppUser> getUserByToken(
            @PathVariable("username") String username, HttpServletRequest request) {
        log.info("Checking the user by token");
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = tokenService.getTokenByHeader(authorizationHeader);
        //tokenService.throwTokenNotMatchUsername(token, username);
        AppUser user =  userService.getUserByUsername(username);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping(CustomURL.tokenRefreshUrl)
    public void refreshToken(
                HttpServletRequest request,
                HttpServletResponse response) {
        log.info("Refreshing the token");



        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refreshToken = tokenService.getTokenByHeader(authorizationHeader);
        System.out.println(refreshToken);
        try {
            String username = tokenService.getUsernameByToken(refreshToken);

            AppUser appUser = userService.getUserByUsername(username);
            String accessToken = tokenService.createAccessToken(
                    appUser.getUsername(),
                    ServletUtil.getUrlByRequest(request),
                    appUser.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList())
            );

            log.info("sending the token");
            ServletUtil.userOkMessage(request, response, appUser, accessToken, refreshToken);
        } catch (TokenExpiredException ex) {
            ServletUtil.errorResponse(request, response,
                    HttpStatus.GONE, CustomStatus.TOKEN_EXPIRED);
        }

    }

/*
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception exception) {
            response.setHeader("error", exception.getMessage());
            //response.sendError(FORBIDDEN.value());
            response.setStatus(FORBIDDEN.value());
            Map<String, String> error = new HashMap<>();
            error.put("error_message", exception.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }

 */



}
