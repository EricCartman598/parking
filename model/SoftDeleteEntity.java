package com.epam.parking.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
abstract class SoftDeleteEntity extends BaseEntity {

    @Column(name = "is_deleted", columnDefinition = "boolean DEFAULT false")
    private boolean isDeleted;
}
