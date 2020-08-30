package com.epam.parking.dto.create;

import com.epam.parking.util.LicensePlateValidation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@LicensePlateValidation
public class VehicleCreateDto {

    @NotNull
    @JsonProperty("license_plate")
    private String licensePlate;

    private String color;

    @NotNull
    @JsonProperty("vehicle_brand_title")
    private String vehicleBrandTitle;

    @JsonProperty("vehicle_model_title")
    private String vehicleModelTitle;

    @NotNull
    @JsonProperty("vehicle_type_title")
    private String vehicleTypeTitle;
}
