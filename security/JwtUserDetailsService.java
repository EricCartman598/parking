package com.epam.parking.security;

import com.epam.parking.common.Roles;
import com.epam.parking.model.Manager;
import com.epam.parking.model.Permission;
import com.epam.parking.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Permissions;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUserDetailsService implements UserDetailsService {

    private final ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Manager> manager = managerRepository.findByEmail(email);
        Optional<List<Permission>> permitList = manager.map(Manager::getPermissions);
        List<String> roles = permitList.map(permissions -> permissions.stream()
                .map(permission -> permission.getPermissionType().getTitle())
                .collect(Collectors.toList()))
                .orElse(Collections.singletonList(Roles.ROLE_USER.toString()));

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());

        return new User(email, "", authorities);
    }
}
