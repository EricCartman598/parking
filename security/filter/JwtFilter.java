package com.epam.parking.security.filter;

import com.epam.parking.common.Constants;
import com.epam.parking.config.CommonSecurityConfig;
import com.epam.parking.security.JwtAuthentication;
import com.epam.parking.security.TokenAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtFilter extends OncePerRequestFilter {
    private static AntPathMatcher matcher = new AntPathMatcher();
    private final TokenAuthenticationProvider tokenAuthenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = httpServletRequest.getRequestURI();
        for (String patternStr : CommonSecurityConfig.AUTH_WHITELIST) {
            if (matcher.match(patternStr, uri)) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
        }

        String token = httpServletRequest.getHeader(Constants.TOKEN_HEADER);
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(token));
        httpServletResponse.addHeader(Constants.TOKEN_HEADER, token);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
