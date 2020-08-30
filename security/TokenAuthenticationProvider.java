package com.epam.parking.security;

import com.epam.parking.common.Constants;
import com.epam.parking.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationProvider implements AuthenticationProvider {
    private final RsaVerifier rsaVerifier;
    private final JwtDecodingService jwtDecodingService;
    private final JwtUserDetailsService jwtUserDetailsService;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        try {
            String token = ((JwtAuthentication) auth).getToken();
            Jwt jwt = decodeAndValidate(token);
            UserDetails user = jwtUserDetailsService.loadUserByUsername(getUserEmail(jwt));
            ((JwtAuthentication) auth).setUserDetails(user);
            auth.setAuthenticated(true);
            return auth;
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthentication.class
                .isAssignableFrom(authentication));
    }

    private String getUserEmail(Jwt jwt) {
        Map<String, String> map = jwtDecodingService.getUserInfoByToken(jwt);
        return map.get(Constants.EMAIL).toLowerCase();
    }

    private Jwt decodeAndValidate(String token) {
        if (token == null ||
                token.isEmpty() ||
                token.equals(Constants.STR_NULL)) {
            throw new InvalidTokenException();
        }

        Jwt jwt = JwtHelper.decodeAndVerify(token, rsaVerifier);
        String expiredToken = jwtDecodingService.getUserInfoByToken(jwt).get(Constants.TOKEN_EXPIRED_TIME);
        long expiredTimeStamp = Long.valueOf(expiredToken) * Time.APR_MSEC_PER_USEC;
        long currentTimeStamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
        if (currentTimeStamp > expiredTimeStamp) {
            throw new InvalidTokenException();
        }

        return jwt;
    }
}
