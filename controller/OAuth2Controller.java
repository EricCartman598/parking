package com.epam.parking.controller;

import com.epam.parking.common.RequestsInfo;
import com.epam.parking.security.Token;
import com.epam.parking.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@Profile("security")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @GetMapping(RequestsInfo.AUTHORITIES)
    public Map<String, Object> checkPermission() {
        return oAuth2Service.getAuthorities();
    }

    @GetMapping(RequestsInfo.TOKEN)
    public Token getToken(@RequestParam String redirect_uri, @RequestParam String code) {
        return oAuth2Service.generateToken(redirect_uri, code);
    }
}
