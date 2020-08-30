package com.epam.parking.repository;

import com.epam.parking.model.LeaveReason;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveReasonRepository extends CrudRepository<LeaveReason, Long> {
    Optional<LeaveReason> findByTitle(String title);
}
