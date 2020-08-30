package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleBrandGetDto extends VehicleBrandWithoutModelGetDto {
    private List<VehicleModelGetDto> models;
}
