package com.epam.parking.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverToPermitUpdateUpdateDto {
    @JsonProperty("driver_id")
    private Long driverId;
    @JsonProperty("permit_update_id")
    private Long permitUpdateId;
}
