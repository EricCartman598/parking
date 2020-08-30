package com.epam.parking.controller;

import com.epam.parking.datahub.model.CdmV2Userinfo;
import com.epam.parking.datahub.service.DatahubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatahubController {
    private final DatahubService datahubService;

    public DatahubController(DatahubService datahubService) {
        this.datahubService = datahubService;
    }

    @GetMapping("/email/{email}")
    public CdmV2Userinfo getPersonByEmail(@PathVariable String email) {
        return datahubService.getPersonByEmail(email);
    }
}
