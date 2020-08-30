package com.epam.parking.service;

import com.epam.parking.model.Manager;
import com.epam.parking.model.Office;
import com.epam.parking.model.Permission;
import com.epam.parking.model.PermissionType;
import com.epam.parking.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ManagerService {

    private final ManagerRepository managerRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Manager createManager(String email, PermissionType permissionType, Office office) {
        Manager manager = new Manager(email.toLowerCase(), null);
        Permission permission = new Permission(permissionType, manager, office);
        manager.setPermissions(Collections.singletonList(permission));
        return managerRepository.save(manager);
    }

    public void deleteManagerById(long managerId) {
        managerRepository.deleteById(managerId);
    }
}
