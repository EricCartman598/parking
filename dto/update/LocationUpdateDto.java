package com.epam.parking.dto.update;

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
public class LocationUpdateDto {

    private String title;

    private String summary;

    private List<SpotUpdateDto> spots;

    @JsonProperty("real_capacity")
    private Long realCapacity;

    @JsonProperty("from_date")
    private Long fromDate;

    @JsonProperty("until_date")
    private Long untilDate;
}
