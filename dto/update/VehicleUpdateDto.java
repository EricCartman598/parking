package com.epam.parking.dto.update;

import com.epam.parking.util.LicensePlateValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@LicensePlateValidation
public class VehicleUpdateDto {

    @JsonProperty("license_plate")
    private String licensePlate;

    private String color;

    @JsonProperty("vehicle_brand_title")
    private String vehicleBrandTitle;

    @JsonProperty("vehicle_model_title")
    private String vehicleModelTitle;

    @JsonProperty("vehicle_type_title")
    private String vehicleTypeTitle;
}
