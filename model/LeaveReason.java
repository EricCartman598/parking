package com.epam.parking.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "leave_reasons")
@Getter
@Setter
public class LeaveReason extends BaseEntity {

    @Column(unique = true)
    private String title;

    @OneToMany(mappedBy = "leaveReason")
    private List<PermitUpdateHistory> permitUpdates;
}
