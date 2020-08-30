package com.epam.parking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "vehicle_brands")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleBrand extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "vehicleBrand", fetch = FetchType.LAZY)
    private List<VehicleModel> vehicleModels;

    @OneToMany(mappedBy = "vehicleBrand")
    private List<Vehicle> vehicles;
}
