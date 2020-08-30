package com.epam.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "spots")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Spot extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private long capacity;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_spots_location_id"))
    private Location location;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "permit_updates_spots",
            joinColumns = @JoinColumn(name = "spot_id",
                    foreignKey = @ForeignKey(name = "fk_permit_updates_spots_spot_id")),
            inverseJoinColumns = @JoinColumn(name = "permit_update_id",
                    foreignKey = @ForeignKey(name = "fk_permit_updates_spots_permit_update_id")))
    List<PermitUpdateHistory> linkedPermitUpdates;
}
