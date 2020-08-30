package com.epam.parking.service;

import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.PermissionType;
import com.epam.parking.repository.PermissionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionTypeService {

    private final PermissionTypeRepository permissionTypeRepository;

    @Transactional(readOnly = true)
    public PermissionType findByTitle(String title) {
        return permissionTypeRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(PermissionType.class));
    }
}
