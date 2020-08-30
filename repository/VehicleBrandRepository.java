package com.epam.parking.repository;

import com.epam.parking.model.VehicleBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleBrandRepository extends JpaRepository<VehicleBrand, Long> {
    Optional<VehicleBrand> findByTitle(String title);
}
