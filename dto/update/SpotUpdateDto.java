package com.epam.parking.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpotUpdateDto {

    private long id;

    @NotNull
    private String title;

    @NotNull
    private long capacity;
}
