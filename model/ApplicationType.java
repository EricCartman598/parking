package com.epam.parking.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "application_types")
@Getter
@Setter
public class ApplicationType extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "applicationType")
    private List<Application> applications;
}
