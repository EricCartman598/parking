package com.epam.parking.service;

import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.VehicleType;
import com.epam.parking.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    @Transactional(readOnly = true)
    public VehicleType findByTitle(String title) {
        return vehicleTypeRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(VehicleType.class));
    }
}
