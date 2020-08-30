package com.epam.parking.model;

import lombok.*;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "applications")
@SQLDelete(sql = "UPDATE applications SET is_deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Application extends SoftDeleteEntity {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_applications_driver_id"))
    @NonNull
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "application_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_applications_application_type_id"))
    @NonNull
    private ApplicationType applicationType;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "timestamp DEFAULT current_timestamp")
    @NonNull
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "desired_location_id",
            foreignKey = @ForeignKey(name = "fk_desired_location_id"))
    private Location desiredLocation;
}
