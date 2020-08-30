package com.epam.parking.service;

import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.VehicleCreateDto;
import com.epam.parking.dto.update.VehicleUpdateDto;
import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.Vehicle;
import com.epam.parking.repository.VehicleRepository;
import com.epam.parking.service.specification.BaseSpecification;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelService vehicleModelService;
    private final VehicleBrandService vehicleBrandService;
    private final VehicleTypeService vehicleTypeService;
    private final DriverService driverService;

    @Transactional(readOnly = true)
    public ListForDto<Vehicle> findAll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Vehicle> page = vehicleRepository.findAll(BaseSpecification.hasNotDeleted(), pageable);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(readOnly = true)
    public Vehicle findById(long id) {
        return vehicleRepository.findById(id)
                .filter(vehicle -> !vehicle.isDeleted())
                .orElseThrow(() -> new NotFoundException(Vehicle.class));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Vehicle save(VehicleCreateDto vehicleCreateDto, long driverId) {
        Vehicle vehicle = GenericModelMapper.convertToClass(vehicleCreateDto, Vehicle.class);
        vehicle.setVehicleBrand(vehicleBrandService.findByTitle(vehicleCreateDto.getVehicleBrandTitle()));
        if (vehicleCreateDto.getVehicleModelTitle() != null) {
            vehicle.setVehicleModel(vehicleModelService
                    .findByTitle(vehicleCreateDto.getVehicleModelTitle(), vehicleCreateDto.getVehicleBrandTitle()));
        }
        vehicle.setVehicleType(vehicleTypeService.findByTitle(vehicleCreateDto.getVehicleTypeTitle()));
        vehicle.setDriver(driverService.findById(driverId));
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void deleteById(long id) {
        vehicleRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Vehicle updateById(long id, VehicleUpdateDto vehicleUpdateDto) {
        Vehicle vehicle = GenericModelMapper.convertToClass(vehicleUpdateDto, Vehicle.class);
        Vehicle foundVehicle = vehicleRepository.getOne(id);
        Optional.ofNullable(vehicle.getColor()).ifPresent(foundVehicle::setColor);
        Optional.ofNullable(vehicle.getLicensePlate()).ifPresent(foundVehicle::setLicensePlate);
        Optional.ofNullable(vehicle.getVehicleBrand().getTitle()).ifPresent(vehicleBrandService::findByTitle);
        if (vehicle.getVehicleBrand() != null) {
            foundVehicle.setVehicleBrand(vehicleBrandService.findByTitle(vehicle.getVehicleBrand().getTitle()));
        }
        if (vehicle.getVehicleModel() != null) {
            foundVehicle.setVehicleModel(vehicleModelService.findByTitle(vehicle.getVehicleModel().getTitle(),
                    vehicle.getVehicleBrand().getTitle()));
        }
        if (vehicle.getVehicleType() != null) {
            foundVehicle.setVehicleType(vehicleTypeService.findByTitle(vehicle.getVehicleType().getTitle()));
        }
        vehicleRepository.save(foundVehicle);
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Vehicle.class));
    }
}
