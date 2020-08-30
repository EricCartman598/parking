package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverGetDto {

    private long id;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("started_work")
    private double startedWork;

    @JsonProperty("job_function_track")
    private String track;

    @JsonProperty("job_function_level")
    private String level;

    @JsonProperty("parking_in_office")
    private String parkingInOffice;

    private String office;

    private List<VehicleGetDto> vehicles;

    @JsonProperty("permit_updates")
    private List<PermitUpdateHistoryGetDto> permitUpdatesHistory;

    @JsonProperty("current_permit")
    private PermitUpdateGetDto currentPermit;
}
