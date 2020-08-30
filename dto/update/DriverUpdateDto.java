package com.epam.parking.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DriverUpdateDto {

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("job_function_level")
    private String level;

    private String office;

    @JsonProperty("job_function_track")
    private String track;

    @JsonProperty("parking_in_office")
    private String parkingInOffice;

    @JsonProperty("started_work")
    private Long startedWork;
}
