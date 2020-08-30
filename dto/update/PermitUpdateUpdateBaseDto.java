package com.epam.parking.dto.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PermitUpdateUpdateBaseDto {
    @JsonProperty("permit_type")
    private String permitType;

    @JsonProperty("leave_reason")
    private String leaveReason;

    @JsonProperty("spot_ids")
    private List<Long> spotIds;

    @JsonProperty("updated_at")
    private Long updatedAt;
}
