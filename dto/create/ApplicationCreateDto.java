package com.epam.parking.dto.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationCreateDto {

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("started_work")
    private Long startedWork;

    @JsonProperty("job_function_track")
    private String track;

    @JsonProperty("job_function_level")
    private String level;

    @JsonProperty("parking_in_office")
    private String parkingInOffice;

    @JsonProperty("office")
    private String office;

    @JsonProperty("desired_location")
    private String desiredLocation;

    @Valid
    private List<VehicleCreateDto> vehicles;
}
