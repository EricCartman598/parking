package com.epam.parking.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "locations", uniqueConstraints = @UniqueConstraint(columnNames = {"title", "office_id"}))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Location extends BaseEntity {

    @NonNull
    @Column(nullable = false)
    private String title;

    @Column
    private String summary;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_locations_office_id"))
    private Office office;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Spot> spots;

    @Column(name = "real_capacity")
    private Long realCapacity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_date", columnDefinition = "timestamp DEFAULT null")
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "until_date", columnDefinition = "timestamp DEFAULT null")
    private Date untilDate;

    @OneToMany(mappedBy = "desiredLocation")
    private List<Application> applications;
}
