package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PermitUpdateGetDto {

    private long id;

    @JsonProperty("permit_type")
    private PermitTypeGetDto permitType;

    @JsonProperty("updated_at")
    private double updatedAt;

    @JsonProperty("leave_reason")
    private LeaveReasonGetDto leaveReason;

    @JsonProperty("spots")
    private List<SpotGetDto> spots;
}
