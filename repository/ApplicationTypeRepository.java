package com.epam.parking.repository;

import com.epam.parking.model.ApplicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationTypeRepository extends JpaRepository<ApplicationType, Long> {
    Optional<ApplicationType> findByTitle(String title);
}
