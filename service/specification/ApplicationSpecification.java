package com.epam.parking.service.specification;

import com.epam.parking.model.Application;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import java.util.Date;

public class ApplicationSpecification {

    private ApplicationSpecification() {
    }

    public static Specification<Application> likeEmailOrNameOrLastName(String email) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            return cb.like(root.join("driver").get("email"), "%" + email + "%");
        };
    }

    public static Specification<Application> likeType(String type) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            return cb.like(root.join("applicationType").get("title"), "%" + type + "%");
        };
    }

    public static Specification<Application> likeLicensePlate(String licensePlate) {
        return (root, cq, cb) ->
                cb.like(root.join("driver").join("vehicles", JoinType.LEFT).get("licensePlate"),
                        "%" + licensePlate + "%");
    }

    public static Specification<Application> receiptBefore(Date date) {
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Application> receiptAfter(Date date) {
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<Application> updateBefore(Date date) {
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("updatedAt"), date);
    }

    public static Specification<Application> updateAfter(Date date) {
        return (root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("updatedAt"), date);
    }

    public static Specification<Application> equalApplicationType(String applicationTypeTitle) {
        return (root, cq, cb) -> cb.equal(root.join("applicationType")
                .get("title"), applicationTypeTitle);
    }

    public static Specification<Application> equalOffice(String office) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            return cb.like(root.join("driver").get("office"), "%" + office + "%");
        };
    }
}
