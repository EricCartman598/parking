package com.epam.parking.controller;

import com.epam.parking.common.RequestsInfo;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.get.VehicleBrandGetDto;
import com.epam.parking.dto.get.VehicleModelGetDto;
import com.epam.parking.service.VehicleBrandService;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RequestsInfo.VEHICLES_BRANDS)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleBrandController {

    private final VehicleBrandService vehicleBrandService;

    @GetMapping
    public ListForDto<VehicleBrandGetDto> getAllBrands() {
        return GenericModelMapper.convertList(vehicleBrandService.getAllBrands(), VehicleBrandGetDto.class);
    }

    @GetMapping(RequestsInfo.FULL)
    public ListForDto<VehicleBrandGetDto> getAllBrandModels() {
        return GenericModelMapper.convertList(vehicleBrandService.getAllBrandsWithModels(), VehicleBrandGetDto.class);
    }

    @GetMapping(RequestsInfo.ID)
    public VehicleBrandGetDto getBrandAndModelById(@PathVariable long id) {
        return GenericModelMapper.convertToClass(vehicleBrandService.getVehicleBrandById(id), VehicleBrandGetDto.class);
    }

    @GetMapping(RequestsInfo.ID + RequestsInfo.VEHICLES_MODELS)
    public ListForDto<VehicleModelGetDto> getModelsByBrandId(@PathVariable long id) {
        return GenericModelMapper.convertList(vehicleBrandService.getModelsByBrandId(id), VehicleModelGetDto.class);
    }

    @GetMapping(RequestsInfo.ID + RequestsInfo.VEHICLES_MODELS + RequestsInfo.MODEL_ID)
    public VehicleBrandGetDto getSpecificBrandAndModel(@PathVariable(value = "id") long brandId,
                                                       @PathVariable(value = "modelId") long modelId) {
        return GenericModelMapper.convertToClass(vehicleBrandService.getSpecificBrandAndModel(brandId, modelId),
                VehicleBrandGetDto.class);
    }
}
