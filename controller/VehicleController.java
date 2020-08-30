package com.epam.parking.controller;

import com.epam.parking.common.RequestsInfo;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.VehicleCreateDto;
import com.epam.parking.dto.get.VehicleGetDto;
import com.epam.parking.dto.update.VehicleUpdateDto;
import com.epam.parking.service.VehicleService;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(RequestsInfo.VEHICLES)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ListForDto<VehicleGetDto> allVehicles(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                 @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit) {
        return GenericModelMapper.convertList(vehicleService.findAll(offset, limit), VehicleGetDto.class);
    }

    @GetMapping(RequestsInfo.ID)
    public VehicleGetDto getVehicle(@PathVariable long id) {
        return GenericModelMapper.convertToClass(vehicleService.findById(id), VehicleGetDto.class);
    }

    @PostMapping(RequestsInfo.ID)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public VehicleGetDto createVehicle(@PathVariable long id, @Valid @RequestBody VehicleCreateDto vehicleDto) {
        return GenericModelMapper.convertToClass(vehicleService.save(vehicleDto, id), VehicleGetDto.class);
    }

    @DeleteMapping(RequestsInfo.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public void deleteVehicle(@PathVariable long id) {
        vehicleService.deleteById(id);
    }

    @PutMapping(RequestsInfo.ID)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public VehicleGetDto updateVehicle(@PathVariable long id, @Valid @RequestBody VehicleUpdateDto vehicle) {
        return GenericModelMapper.convertToClass(vehicleService.updateById(id, vehicle), VehicleGetDto.class);
    }
}
