package com.epam.parking.service;

import com.epam.parking.common.ClientSecuiryProperties;
import com.epam.parking.common.Constants;
import com.epam.parking.security.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Setter
public class OAuth2Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Service.class);
    private final RestOperations restOperations;
    private final ClientSecuiryProperties clientSecuiryProperties;

    public Token generateToken(String redirectUri, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientSecuiryProperties.getEpamClientId());
        map.add("redirect_uri", redirectUri);
        map.add("code", code);
        map.add("grant_type", clientSecuiryProperties.getEpamGrantType());
        map.add("client_secret", clientSecuiryProperties.getEpamClientSecret());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<Token> serverResponse =
                restOperations.exchange(clientSecuiryProperties.getEpamAccessTokenUri(), HttpMethod.POST, entity, Token.class);

        return serverResponse.getBody();
    }

    public Map<String, Object> getAuthorities() {
        Map<String, Object> map = new HashMap<>();
        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.split("_")[1].toLowerCase())
                .collect(Collectors.toList());
        map.put(Constants.KEY_AUTHORITIES, authorities);
        return map;
    }
}

