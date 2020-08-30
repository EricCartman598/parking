package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGetDto {

    private long id;

    @JsonProperty("office_title")
    private String officeTitle;

    @JsonProperty("office_id")
    private long officeId;

    @NonNull
    @JsonProperty("permission_type")
    private PermitTypeGetDto permissionType;

    @NotNull
    private ManagerGetDto manager;
}
