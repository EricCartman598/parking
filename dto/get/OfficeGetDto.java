package com.epam.parking.dto.get;

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
public class OfficeGetDto {

    private long id;

    @NotNull
    private String title;

    private List<LocationGetDto> locations;
}
