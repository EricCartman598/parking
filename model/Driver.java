package com.epam.parking.model;

import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "drivers")
@SQLDelete(sql = "UPDATE drivers SET is_deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Driver extends SoftDeleteEntity {

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private Date startedWork;

    @Column
    private String track;

    @Column
    private String level;

    @Column
    private String parkingInOffice;

    @Column
    private String office;

    @NonNull
    @Column(name = "is_applier", nullable = false)
    private boolean isApplier;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    @Where(clause = "is_deleted = false")
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PermitUpdateHistory> permitUpdatesHistory;

    @Transient
    private PermitUpdateHistory currentPermit;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Application> applications;
}

