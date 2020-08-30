package com.epam.parking.repository;

import com.epam.parking.model.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionTypeRepository extends JpaRepository<PermissionType, Long> {
    Optional<PermissionType> findByTitle(String title);
}
