package com.epam.parking.dto.get;

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
public class SpotGetDto {

    private long id;

    private String title;

    private long capacity;

    private List<DriverGetDto> drivers;

    @JsonProperty("location_title")
    private String locationTitle;

    @JsonProperty("location_id")
    private Long locationId;
}
