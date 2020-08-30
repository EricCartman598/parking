package com.epam.parking.dto.create;

import com.epam.parking.dto.get.SpotGetDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermitUpdateCreateDto {

    @JsonProperty("permit_type_title")
    private String permitType;

    @JsonProperty("leave_reason_title")
    private String leaveReason;

    private List<SpotGetDto> spots;
}
