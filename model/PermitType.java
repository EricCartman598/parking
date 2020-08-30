package com.epam.parking.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "permit_types")
@Getter
@Setter
public class PermitType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "permitType")
    private List<PermitUpdateHistory> permitUpdates;
}
