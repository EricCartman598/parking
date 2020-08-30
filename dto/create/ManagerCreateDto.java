package com.epam.parking.dto.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerCreateDto {

    @NotNull
    private String email;

    @NotNull
    private String title;

    @NotNull
    @JsonProperty("office_id")
    private long officeId;
}
