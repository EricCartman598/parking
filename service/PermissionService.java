package com.epam.parking.service;

import com.epam.parking.dto.ListForDto;
import com.epam.parking.model.Permission;
import com.epam.parking.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionService {

    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public ListForDto<Permission> getByOfficeId(long officeId) {
        List<Permission> permissions = permissionRepository.findByOfficeId(officeId);
        return new ListForDto<>(permissions.size(), permissions);
    }

    @Transactional(readOnly = true)
    public ListForDto<Permission> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return new ListForDto<>(permissions.size(), permissions);
    }
}
