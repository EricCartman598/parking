package com.epam.parking.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("client")
@Getter
@Setter
public class ClientSecuiryProperties {
    private String epamClientId;
    private String epamClientSecret;
    private String epamAccessTokenUri;
    private String epamGrantType;
    private String epamSslPath;
}
