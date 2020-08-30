package com.epam.parking.dto.get;

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
public class LocationGetDto {

    private long id;

    @NotNull
    private String title;

    private String summary;

    @NotNull
    @JsonProperty("office_id")
    private long officeId;

    private List<SpotGetDto> spots;

    @JsonProperty("total_capacity")
    private long totalCapacity;

    private long available;

    private long occupied;

    @JsonProperty("real_capacity")
    private Long realCapacity;

    @JsonProperty("from_date")
    private Long fromDate;

    @JsonProperty("until_date")
    private Long untilDate;
}
