package com.epam.parking.dto.update;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PermitUpdateUpdateDto extends PermitUpdateUpdateBaseDto{

    @NotNull
    private long id;
}
