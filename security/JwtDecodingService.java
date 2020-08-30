package com.epam.parking.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jwt.Jwt;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.util.Assert.notNull;

@Slf4j
public class JwtDecodingService {

    public Map<String, String> getUserInfoByToken(Jwt jwt) {
        notNull(jwt, "access token can not be null");

        log.debug("access token: " + jwt.getClaims());

        String jwtClaims = jwt.getClaims();

        log.debug("claims: " + jwtClaims);
        return getPrincipalMap(jwtClaims);
    }

    private Map<String, String> getPrincipalMap(String jwtClaims) {
        notNull(jwtClaims, "Json Web Token claim string can not be null");
        Map<String, String> resultMap;

        try {
            ObjectMapper mapper = new ObjectMapper();
            resultMap = mapper.readValue(jwtClaims, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new NoSuchElementException(e.getMessage());
        }

        return resultMap;
    }
}
