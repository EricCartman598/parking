package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleGetDto {

    private long id;

    @JsonProperty("license_plate")
    private String licensePlate;

    private String color;

    @JsonProperty("vehicle_brand")
    private VehicleBrandWithoutModelGetDto vehicleBrand;

    @JsonProperty("vehicle_model")
    private VehicleModelGetDto vehicleModel;

    @JsonProperty("vehicle_type")
    private VehicleTypeGetDto vehicleType;
}
