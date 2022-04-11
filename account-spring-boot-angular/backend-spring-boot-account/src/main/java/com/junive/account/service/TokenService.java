package com.junive.account.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.junive.account.exception.CustomExceptionInvalid;
import com.junive.account.exception.CustomExceptionNotFound;
import com.junive.account.util.CustomStatus;
import com.junive.account.util.ServletUtil;
import com.junive.account.util.CustomURL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    public String createAccessToken(String username, String issuer, List<?> roles) {
        return this.createToken(username, 10, issuer, roles);
    }

    public String createRefreshToken(String username, String issuer) {
        return this.createToken(username, 200, issuer, null);
    }

    public String createToken(String username, int minutes, String issuer, List<?> roles) {
        JWTCreator.Builder token = JWT.create();
        Date expireDate = new Date(System.currentTimeMillis() + (minutes) * 10 * 1000);
        token.withSubject(username) // unique
                .withExpiresAt(expireDate) // 10 mn to access token
                .withIssuer(issuer);
        if (roles != null) token.withClaim("roles", roles);
        return token.sign(ServletUtil.getAlgo());
    }

    public DecodedJWT getDecodedJWTByToken(String token) {
        JWTVerifier verifier = JWT.require(ServletUtil.getAlgo()).build();
        return verifier.verify(token); // Token is valid
    }

    public String getUsernameByToken(String token) {
        return getDecodedJWTByToken(token).getSubject(); // username that come with token
    }

    public String[] getRolesByToken(String token) {
        return getDecodedJWTByToken(token)
                .getClaim("roles").asArray(String.class);
    }

    public String getTokenByHeader(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw CustomExceptionNotFound.builder()
                    .customStatus(CustomStatus.HEADER_AUTHORIZATION_NOT_FOUND).build();
        }
        if (!authorizationHeader.startsWith(CustomURL.tokenStartName)) {
            throw CustomExceptionInvalid.builder()
                    .customStatus(CustomStatus.TOKEN_START_INVALID).build();
        }
        return authorizationHeader.substring(CustomURL.tokenStartName.length());
    }
/*
    public void throwTokenNotMatchUsername(String token, String username) {
        String tokenUsername = this.getUsernameByToken(token);
        if (!tokenUsername.equals(username)) {
            throw CustomExceptionConflict.builder()
                    .status(CustomStatus.TOKEN_USERNAMES_NOT_MATCH)
                    .wrongs(List.of(username, tokenUsername)).build();
        }
    }
*/
}
