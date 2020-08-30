package com.epam.parking.dto.create;

import com.epam.parking.dto.get.SpotGetDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationCreateDto {

    @NotNull
    private String title;

    private String summary;

    @NotNull
    @JsonProperty("office_id")
    private long officeId;

    @NotNull
    private long capacity;

    private List<SpotGetDto> spots;

    @JsonProperty("real_capacity")
    private Long realCapacity;

    @JsonProperty("from_date")
    private Long fromDate;

    @JsonProperty("until_date")
    private Long untilDate;
}
