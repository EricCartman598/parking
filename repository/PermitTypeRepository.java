package com.epam.parking.repository;

import com.epam.parking.model.PermitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermitTypeRepository extends JpaRepository<PermitType, Long> {
    Optional<PermitType> findByTitle(String title);
}
