package com.epam.parking.dto.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import java.util.List;

@Getter
@Setter
public class FilteredApplicationGetDto {

    @JsonProperty("application_status")
    private List<String> applicationTypeTitle;

    @JsonProperty("date_of_receipt_from")
    @DecimalMin("0.0")
    private Double dateOfReceiptFrom;

    @JsonProperty("date_of_receipt_to")
    @DecimalMin("0.0")
    private Double dateOfReceiptTo;

    @JsonProperty("date_of_update_from")
    @DecimalMin("0.0")
    private Double dateOfUpdateFrom;

    @JsonProperty("date_of_update_to")
    @DecimalMin("0.0")
    private Double dateOfUpdateTo;

    @JsonProperty("office")
    private String office;
}
