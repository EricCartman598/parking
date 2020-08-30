package com.epam.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "vehicle_models")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleModel extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "vehicle_brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehicle_models_vehicle_brand_id"))
    private VehicleBrand vehicleBrand;

    @OneToMany(mappedBy = "vehicleModel")
    private List<Vehicle> vehicles;
}
