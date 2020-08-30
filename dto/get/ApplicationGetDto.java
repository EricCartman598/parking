package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationGetDto {

    private long id;
    private DriverGetDto driver;

    @JsonProperty("application_type")
    private ApplicationTypeGetDto applicationType;

    @JsonProperty("created_at")
    private double createdAt;

    @JsonProperty("updated_at")
    private double updatedAt;

    @JsonProperty("desired_location")
    private String desiredLocation;
}
