package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilteredDriverGetDto {

    @JsonProperty("permit_type")
    private String permitType;

    private List<String> locations;

    @JsonProperty("applied_from")
    @DecimalMin("0.0")
    private Double appliedFrom;

    @JsonProperty("applied_until")
    @DecimalMin("0.0")
    private Double appliedUntil;

    @JsonProperty("issued_from")
    @DecimalMin("0.0")
    private Double issuedFrom;

    @JsonProperty("issued_until")
    @DecimalMin("0.0")
    private Double issuedUntil;

    @JsonProperty("left_from")
    @DecimalMin("0.0")
    private Double leftFrom;

    @JsonProperty("left_until")
    @DecimalMin("0.0")
    private Double leftUntil;

    @JsonProperty("leave_reason")
    private String leaveReason;

    @JsonProperty("vehicle_brands")
    private List<String> vehicleBrands;

    @JsonProperty("vehicle_models")
    private List<String> vehicleModels;
}