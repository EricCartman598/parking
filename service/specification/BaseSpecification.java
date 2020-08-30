package com.epam.parking.service.specification;

import org.springframework.data.jpa.domain.Specification;

public class BaseSpecification {

    private BaseSpecification() {
    }

    public static <T> Specification<T> hasNotDeleted() {
        return (root, cq, cb) -> cb.equal(root.get("isDeleted"), false);
    }
}
