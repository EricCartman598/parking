package com.epam.parking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Profile("!security")
public class DisabledWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .configurationSource(corsConfigurationSource)
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/**")
                .permitAll();
    }
}
