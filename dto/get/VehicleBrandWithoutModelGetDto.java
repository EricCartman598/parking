package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleBrandWithoutModelGetDto {
    private long id;

    private String title;
}
