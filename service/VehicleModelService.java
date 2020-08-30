package com.epam.parking.service;

import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.VehicleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleModelService {

    private final VehicleBrandService vehicleBrandService;

    @Transactional(readOnly = true)
    public VehicleModel findByTitle(String title, String brandTitle) {
        return vehicleBrandService.findByTitle(brandTitle).getVehicleModels().stream()
                .filter(vehicleModel -> vehicleModel.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(VehicleModel.class));
    }
}
