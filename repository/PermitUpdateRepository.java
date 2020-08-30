package com.epam.parking.repository;

import com.epam.parking.model.PermitUpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermitUpdateRepository extends JpaRepository<PermitUpdateHistory, Long> {
    Optional<PermitUpdateHistory> findByDriverId(long id);
}
