package com.epam.parking.security;

import com.epam.parking.common.ClientSecuiryProperties;
import com.epam.parking.common.Roles;
import com.epam.parking.config.CommonSecurityConfig;
import com.epam.parking.exception.InvalidSslFileException;
import com.epam.parking.repository.ManagerRepository;
import com.epam.parking.security.filter.JwtFilter;
import com.epam.parking.service.OAuth2Service;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Profile("security")
@Getter
@Setter
@EnableConfigurationProperties(ClientSecuiryProperties.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final CorsConfigurationSource corsConfigurationSource;
    private final ManagerRepository managerRepository;
    private final RestOperations restOperations;
    private final String[] ENDPOINTS_FOR_ADMIN = {
            "/applications*/**",
            "/drivers**",
            "/statistics**",
            "/capacity**"
    };
    private final String[] ENDPOINTS_FOR_SUPERVISOR = {
            "/supervisor**"
    };
    private final String[] ENDPOINTS_FOR_MANAGER = {
            "/applications*/**",
            "/drivers**",
            "/statistics**",
            "/capacity**"
    };
    private final String[] ENDPOINTS_FOR_USER = {
            "/applications*",
            "/drivers/{\\d}"
    };
    private final ClientSecuiryProperties clientSecuiryProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .configurationSource(corsConfigurationSource)
                .and()
                .csrf()
                .disable()
                .addFilterBefore(jwtFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(ENDPOINTS_FOR_USER).hasAnyRole(Roles.ROLE_ADMIN.toString(), Roles.ROLE_SUPERVISOR.toString(), Roles.ROLE_MANAGER.toString(), Roles.ROLE_USER.toString())
                .antMatchers(ENDPOINTS_FOR_SUPERVISOR).hasRole(Roles.ROLE_SUPERVISOR.toString())
                .antMatchers(ENDPOINTS_FOR_ADMIN).hasAnyRole(Roles.ROLE_ADMIN.toString(), Roles.ROLE_SUPERVISOR.toString())
                .antMatchers(ENDPOINTS_FOR_MANAGER).hasAnyRole(Roles.ROLE_ADMIN.toString(), Roles.ROLE_SUPERVISOR.toString(), Roles.ROLE_MANAGER.toString())
                .antMatchers(CommonSecurityConfig.AUTH_WHITELIST)
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(tokenAuthenticationProvider());
    }

    @Bean
    public OAuth2Service oAuth2Service() {
        return new OAuth2Service(restOperations, clientSecuiryProperties);
    }

    @Bean
    public TokenAuthenticationProvider tokenAuthenticationProvider() {
        return new TokenAuthenticationProvider(rsaVerifier(), jwtDecodingService(), jwtUserDetailsService());
    }

    @Bean
    public RsaVerifier rsaVerifier() {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(clientSecuiryProperties.getEpamSslPath()));
            return new RsaVerifier(new String(encoded, Charset.defaultCharset()));
        } catch (IOException e) {
            throw new InvalidSslFileException();
        }
    }

    @Bean
    public JwtUserDetailsService jwtUserDetailsService() {
        return new JwtUserDetailsService(managerRepository);
    }

    @Bean
    public JwtDecodingService jwtDecodingService() {
        return new JwtDecodingService();
    }
}
