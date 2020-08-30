package com.epam.parking.repository;

import com.epam.parking.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {

    String MINIMAL_DATE = "\'1970-01-01 03:00:00\'";

    Optional<Driver> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query(value = "update drivers d " +
            "set email = COALESCE(CAST(:email AS TEXT), d.email), " +
            "phone_number = COALESCE(CAST(:phone_number AS TEXT), d.phone_number), " +
            "started_work=CASE WHEN cast(:started_work as timestamp) = "+ MINIMAL_DATE +
            " THEN d.started_work ELSE :started_work END, " +
            "track = COALESCE(CAST(:track AS TEXT), d.track), " +
            "level = COALESCE(CAST(:level AS TEXT), d.level), " +
            "parking_in_office = COALESCE(CAST(:parking_in_office AS TEXT), d.parking_in_office), " +
            "office = COALESCE(CAST(:office AS TEXT), d.office)" +
            " where d.id= :id", nativeQuery = true)
    void updateViaQuery(@Param("id")Long id, @Param("email") String email, @Param("phone_number") String phoneNumber,
                        @Param("started_work") Date startedWork, @Param("track") String track,
                        @Param("level") String level, @Param("parking_in_office") String parkingInOffice,
                        @Param("office") String office);
}
