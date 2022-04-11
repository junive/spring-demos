package com.junive.account.util;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junive.account.model.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServletUtil {

    public static Algorithm getAlgo() {
        return Algorithm.HMAC256("secret".getBytes()); // Not to do in prod
    }

    public static String getUrlByRequest(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    public static URI getUriByPath(String path) {
        return URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path(path).toUriString());
    }

    public static void okResponse(HttpServletResponse response, Object message) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), message);
        } catch(IOException ex) {
            log.info("an error occurred writing a response");
            ex.printStackTrace();
        }
    }
    public static void errorResponse(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpStatus status,
                                      CustomStatus customStatus,
                                      Object badValue) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        Map<String, Object> message = new HashMap<>();
        message.put("bad_value", badValue);
        message.put("custom_status", customStatus);
        message.put("request_path", request.getRequestURL().toString());
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), message);
        } catch(IOException ex) {
            log.info("an error occurred writing a response");
            ex.printStackTrace();
        }
    }

    public static void errorResponse(HttpServletRequest request,
                                      HttpServletResponse response,
                                      HttpStatus status,
                                      CustomStatus customStatus) {
        errorResponse(request, response, status, customStatus, "null");
    }

    public static void userOkMessage(HttpServletRequest request,
                                     HttpServletResponse response,
                                     AppUser appUser,
                                     String accessToken,
                                     String refreshToken ) {


        Map<String, Object> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        tokens.put("user_response", appUser);
        response.setContentType(APPLICATION_JSON_VALUE);
        ServletUtil.okResponse(response, tokens);
    }
}
