package com.epam.parking.dto.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverCreateDto {

    @NotNull
    private String email;

    @NotNull
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotNull
    @JsonProperty("permit_type_title")
    private String permitTypeTitle;

    @JsonProperty("leave_reason")
    private String leaveReason;

    @Valid
    private List<VehicleCreateDto> vehicles;
}
