package com.epam.parking.model;

import com.epam.parking.util.LicensePlateValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles SET is_deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@LicensePlateValidation
public class Vehicle extends SoftDeleteEntity {

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    private String color;

    @ManyToOne
    @JoinColumn(name = "vehicle_brand_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehicles_vehicle_brand_id"))
    private VehicleBrand vehicleBrand;

    @ManyToOne
    @JoinColumn(name = "vehicle_model_id",
            foreignKey = @ForeignKey(name = "fk_vehicles_vehicle_model_id"))
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehicles_vehicle_type_id"))
    private VehicleType vehicleType;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vehicles_driver_id"))
    private Driver driver;
}
