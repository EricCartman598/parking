package com.epam.parking.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class BatchPermitUpdateUpdateDto {
    @NotNull
    @JsonProperty("driver_and_permit_update_id")
    private List<DriverToPermitUpdateUpdateDto> driverAndPermitUpdateId;

    @JsonProperty("permit_update")
    private PermitUpdateUpdateBaseDto permitUpdateUpdateBaseDto;
}
