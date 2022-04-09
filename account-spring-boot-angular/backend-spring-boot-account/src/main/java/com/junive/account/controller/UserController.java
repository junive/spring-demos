package com.junive.account.controller;

import com.junive.account.model.AppUser;
import com.junive.account.model.Role;
import com.junive.account.model.RoleRequest;
import com.junive.account.util.ServletUtil;
import com.junive.account.service.TokenService;
import com.junive.account.service.UserService;
import com.junive.account.util.CustomText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;


    @GetMapping(CustomText.allUserUrl)
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping(CustomText.saveUserUrl)
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI url = ServletUtil.getUriByPath(CustomText.saveUserUrl);
        return ResponseEntity.created(url).body(userService.saveUser(user));
    }

    @PostMapping(CustomText.saveRoleUrl)
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI url  = ServletUtil.getUriByPath( CustomText.saveRoleUrl);
        return ResponseEntity.created(url).body(userService.saveRole(role));
    }

    @PostMapping( CustomText.addRoleUrl)
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleRequest request) {
        userService.addRoleToUser(request.username(), request.roleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(CustomText.findUserUrl+ "/{username}")
    public ResponseEntity<AppUser> getUserByToken(
            @PathVariable("username") String username, HttpServletRequest request) {
        log.info("Checking the user by token");
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String token = tokenService.getTokenByHeader(authorizationHeader);
        //tokenService.throwTokenNotMatchUsername(token, username);
        AppUser user =  userService.getUserByUsername(username);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping(CustomText.tokenRefreshUrl)
    public  ResponseEntity<Map<String, String>> refreshToken(HttpServletRequest request)  {
        log.info("Refreshing the token");

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refreshToken = tokenService.getTokenByHeader(authorizationHeader);
        String username = tokenService.getUsernameByToken(refreshToken);
        AppUser user = userService.getUserByUsername(username);
        String accessToken = tokenService.createAccessToken(
                user.getUsername(),
                ServletUtil.getUrlByRequest(request),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );

        Map<String, String> tokens = new HashMap<>();
        tokens.put(CustomText.tokenAccessName, accessToken);
        tokens.put(CustomText.tokenRefreshName, refreshToken);

        log.info("sending the token");
        return ResponseEntity.ok().body(tokens);

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


}
