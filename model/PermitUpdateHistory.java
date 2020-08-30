package com.epam.parking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "permit_updates_history")
@Getter
@Setter
@NoArgsConstructor
public class PermitUpdateHistory extends BasePermitUpdate {

    @NonNull
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_permit_updates_driver_id"))
    private Driver driver;

    @Column
    boolean isCurrent = false;

    public PermitUpdateHistory(PermitUpdateHistory permitUpdateHistory, Date updatedDate) {
        super(permitUpdateHistory.getPermitType());
        this.setPermitType(permitUpdateHistory.getPermitType());
        this.setDriver(permitUpdateHistory.getDriver());
        this.setLeaveReason(permitUpdateHistory.getLeaveReason());
        if(permitUpdateHistory.getSpots() != null) {
            this.setSpots(new ArrayList<>(permitUpdateHistory.getSpots()));
        }
        this.setUpdatedAt(updatedDate);
    }

    public PermitUpdateHistory(@NonNull Driver driver, @NonNull PermitType permitType) {
        super(permitType);
        this.driver = driver;
    }

    @ManyToMany(mappedBy = "linkedPermitUpdates", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Spot> spots = new ArrayList<>();
}