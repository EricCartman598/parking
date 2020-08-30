package com.epam.parking.service.specification;

import com.epam.parking.common.Constants;
import com.epam.parking.model.Driver;
import com.epam.parking.model.PermitUpdateHistory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;

public class DriverSpecification {

    private DriverSpecification() {
    }

    public static Specification<Driver> likeEmail(String email) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            return cb.like(cb.lower(root.get(Constants.EMAIL)), "%" + email + "%");
        };
    }

    public static Specification<Driver> likeLicensePlate(String licensePlate) {
        return (root, cq, cb) ->
                cb.like(root.join(Constants.VEHICLES, JoinType.LEFT)
                        .get(Constants.LICENSE_PLATE), "%" + licensePlate + "%");
    }

    public static Specification<Driver> equalLocation(Long locationId) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            result = result.join(Constants.SPOTS, JoinType.LEFT);
            result = result.join(Constants.LOCATION, JoinType.LEFT);
            return cb.equal(result.get(Constants.ID), locationId);
        };
    }

    public static Specification<Driver> equalLocation(String locationName) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            result = result.join(Constants.SPOTS, JoinType.LEFT);
            result = result.join(Constants.LOCATION, JoinType.LEFT);
            return cb.equal(result.get(Constants.TITLE), locationName);
        };
    }

    public static Specification<Driver> equalSpot(Long SpotId) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            return cb.equal(result.join(Constants.SPOTS).get(Constants.ID), SpotId);
        };
    }

    public static Specification<Driver> equalPermitType(String permitType) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            return cb.equal(result.get(Constants.PERMIT_TYPE).get(Constants.TITLE), permitType);
        };
    }

    public static Specification<Driver> hasPermitTypeInHistory(String permitUpdateTitle) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverPermitUpdateHistoryJoin(root, cb);
            result = result.on(cb.equal(result.get(Constants.PERMIT_TYPE).get(Constants.TITLE), permitUpdateTitle));
            return cq.where(cb.equal(result.get(Constants.PERMIT_TYPE).get(Constants.TITLE), permitUpdateTitle)).getRestriction();
        };
    }

    public static Specification<Driver> gotCurrentPermitBefore(Date date) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            return cb.lessThanOrEqualTo(result.get(Constants.UPDATED_AT), date);
        };
    }

    public static Specification<Driver> gotCurrentPermitAfter(Date date) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            return cb.greaterThanOrEqualTo(result.get(Constants.UPDATED_AT), date);
        };
    }

    public static Specification<Driver> gotPermitInHistoryBefore(Date date) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverPermitUpdateHistoryJoin(root, cb);
            return cb.lessThanOrEqualTo(result.get(Constants.UPDATED_AT), date);
        };
    }

    public static Specification<Driver> gotPermitInHistoryAfter(Date date) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverPermitUpdateHistoryJoin(root, cb);
            return cb.greaterThanOrEqualTo(result.get(Constants.UPDATED_AT), date);
        };
    }

    public static Specification<Driver> hasLeaveReasonCurrentPermit(String leaveReason) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverCurrentPermitUpdateJoin(root, cb);
            return cb.equal(result.get(Constants.LEAVE_REASON).get(Constants.TITLE), leaveReason);
        };
    }

    public static Specification<Driver> hasLeaveReasonInPermitHistory(String leaveReason) {
        return (root, cq, cb) -> {
            Join<Driver, PermitUpdateHistory> result = getDriverPermitUpdateHistoryJoin(root, cb);
            result = result.on(cb.equal(result.get(Constants.LEAVE_REASON).get(Constants.TITLE), leaveReason));
            return cq.where(cb.equal(result.get(Constants.LEAVE_REASON).get(Constants.TITLE), leaveReason)).getRestriction();
        };
    }

    public static Specification<Driver> hasVehicleBrand(String vehicleBrand) {
        return (root, cq, cb) -> cb.equal(root.join(Constants.VEHICLES)
                .get(Constants.VEHICLE_BRAND)
                .get(Constants.TITLE), vehicleBrand);
    }

    public static Specification<Driver> hasVehicleModel(String vehicleModel) {
        return (root, cq, cb) -> cb.equal(root.join(Constants.VEHICLES)
                .get(Constants.VEHICLE_MODEL)
                .get(Constants.TITLE), vehicleModel);
    }

    private static Join<Driver, PermitUpdateHistory> getDriverCurrentPermitUpdateJoin(Root<Driver> root, CriteriaBuilder cb) {
        Join<Driver, PermitUpdateHistory> result = getDriverPermitUpdateHistoryJoin(root, cb);
        result = result.on(cb.equal(result.get(Constants.IS_CURRENT), true));
        return result;
    }

    private static Join<Driver, PermitUpdateHistory> getDriverPermitUpdateHistoryJoin(Root<Driver> root, CriteriaBuilder cb) {
        Join<Driver, PermitUpdateHistory> result = root.join(Constants.PERMIT_UPDATES_HISTORY, JoinType.LEFT);
        result = result.on(cb.equal(result.get(Constants.ID), result.get(Constants.DRIVER)));
        return result;
    }
}
