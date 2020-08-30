package com.epam.parking.service;

import com.epam.parking.dto.ListForDto;
import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.VehicleBrand;
import com.epam.parking.model.VehicleModel;
import com.epam.parking.repository.VehicleBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleBrandService {

    private final VehicleBrandRepository vehicleBrandRepository;

    @Transactional(readOnly = true)
    public VehicleBrand findByTitle(String title) {
        return vehicleBrandRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(VehicleBrand.class));
    }

    @Transactional(readOnly = true)
    public ListForDto<VehicleBrand> getAllBrandsWithModels() {
        List<VehicleBrand> vehicleBrands = vehicleBrandRepository
                .findAll(new Sort(Sort.Direction.ASC, "title"));

        vehicleBrands
                .forEach(v -> v.getVehicleModels()
                        .sort(Comparator.comparing(VehicleModel::getTitle)));

        return new ListForDto<>(vehicleBrands.size(), vehicleBrands);
    }

    @Transactional(readOnly = true)
    public ListForDto<VehicleBrand> getAllBrands() {
        List<VehicleBrand> vehicleBrands = getAllBrandsWithModels().getResults();
        vehicleBrands.forEach(vehicleBrandGetDto -> vehicleBrandGetDto.setVehicleModels(null));
        return new ListForDto<>(vehicleBrands.size(), vehicleBrands);

    }

    @Transactional(readOnly = true)
    public VehicleBrand getVehicleBrandById(long id) {
        return vehicleBrandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(VehicleBrand.class));
    }

    @Transactional(readOnly = true)
    public ListForDto<VehicleModel> getModelsByBrandId(long id) {
        VehicleBrand vehicleBrand = vehicleBrandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(VehicleBrand.class));
        return new ListForDto<>(vehicleBrand.getVehicleModels().size(), vehicleBrand.getVehicleModels());
    }

    @Transactional(readOnly = true)
    public VehicleBrand getSpecificBrandAndModel(long brandId, long modelId) {
        VehicleBrand vehicleBrand = getVehicleBrandById(brandId);
        vehicleBrand.setVehicleModels(vehicleBrand.getVehicleModels()
                .stream()
                .filter(vehicleModel -> vehicleModel.getId() == modelId)
                .collect(Collectors.toList()));
        return vehicleBrand;
    }
}
