package com.epam.parking.controller;

import com.epam.parking.common.RequestsInfo;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.DriverCreateDto;
import com.epam.parking.dto.create.PermitUpdateCreateDto;
import com.epam.parking.dto.get.DriverGetDto;
import com.epam.parking.dto.get.FilteredDriverGetDto;
import com.epam.parking.dto.get.PermitTypeGetDto;
import com.epam.parking.dto.get.VehicleGetDto;
import com.epam.parking.dto.update.BatchPermitUpdateUpdateDto;
import com.epam.parking.dto.update.DriverUpdateDto;
import com.epam.parking.dto.update.PermitUpdateUpdateDto;
import com.epam.parking.service.DriverService;
import com.epam.parking.service.specification.SpecificationParameterType;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(RequestsInfo.DRIVERS)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ListForDto<DriverGetDto> getDrivers(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                               @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                               @RequestParam(defaultValue = RequestsInfo.DEFAULT_SEARCH_VALUE) String search) {
        return GenericModelMapper.convertList(
                driverService.findAllByEmailOrLicensePlate(offset, limit, search), DriverGetDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public DriverGetDto createDriver(@Valid @RequestBody DriverCreateDto driverDto) {
        return GenericModelMapper.convertToClass(driverService.save(driverDto), DriverGetDto.class);
    }

    @GetMapping(RequestsInfo.ID)
    public DriverGetDto getDriver(@PathVariable long id) {
        return GenericModelMapper.convertToClass(driverService.findById(id), DriverGetDto.class);
    }

    @GetMapping(RequestsInfo.CHECK_EMAIL)
    public Boolean getDriverWithCertainEmail(@RequestParam String email) {
        return driverService.checkByEmail(email);
    }

    @PutMapping(RequestsInfo.ID)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public DriverGetDto updateDriver(@PathVariable long id, @RequestBody DriverUpdateDto driver) {
        return GenericModelMapper.convertToClass(driverService.updateById(id, driver), DriverGetDto.class);
    }

    @DeleteMapping(RequestsInfo.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public void deleteDriver(@PathVariable long id) {
        driverService.deleteById(id);
    }

    @GetMapping(RequestsInfo.ID + RequestsInfo.VEHICLES)
    public ListForDto<VehicleGetDto> getDriverVehicles(@PathVariable long id) {
        return GenericModelMapper.convertList(driverService.findAllCarsById(id), VehicleGetDto.class);
    }

    @PostMapping(RequestsInfo.ID + RequestsInfo.PERMIT_URDATES)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public DriverGetDto createPermitOfDriver(@PathVariable long id,
                                             @RequestBody PermitUpdateCreateDto permitUpdateCreateDto) {
        return GenericModelMapper.convertToClass(
                driverService.addPermitUpdate(id, permitUpdateCreateDto), DriverGetDto.class);
    }

    @PutMapping(RequestsInfo.ID + RequestsInfo.PERMIT_URDATES)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public DriverGetDto updatePermitOfDriver(@PathVariable long id,
                                             @RequestBody PermitUpdateUpdateDto permitUpdateUpdateDto) {
        return GenericModelMapper.convertToClass(
                driverService.updatePermitUpdate(id, permitUpdateUpdateDto), DriverGetDto.class);
    }

    @PutMapping(RequestsInfo.PERMIT_URDATES)
    public ListForDto<DriverGetDto> updatePermitOfDrivers(@RequestBody List<BatchPermitUpdateUpdateDto> batchPermitUpdateUpdateDtos) {
        return GenericModelMapper.convertList(driverService.updateBatchPermitUpdateList(batchPermitUpdateUpdateDtos), DriverGetDto.class);
    }

    @GetMapping(RequestsInfo.PERMIT_TYPE)
    public ListForDto<DriverGetDto> getFilteredDrivers(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                       @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                                       @RequestParam String permitType,
                                                       @RequestParam(defaultValue = RequestsInfo.DEFAULT_SEARCH_VALUE) String search) {
        return GenericModelMapper.convertList(
                driverService.getDriversFilteredByPermitType(offset, limit, permitType, search), DriverGetDto.class);
    }

    @PostMapping(RequestsInfo.LOCATIONS)
    public ListForDto<DriverGetDto> getDriversByLocations(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                          @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                                          @RequestParam(defaultValue = RequestsInfo.DEFAULT_SEARCH_VALUE) String search,
                                                          @RequestBody List<Long> locations) {
        return GenericModelMapper.convertList(
                driverService.findAllByParameter(offset, limit, locations, SpecificationParameterType.LOCATION, search), DriverGetDto.class);
    }

    @PutMapping(RequestsInfo.FILTERED_DRIVERS)
    public ListForDto<DriverGetDto> getDriversFilteredByMultipleFields(@Valid @RequestBody FilteredDriverGetDto filteredDriverGetDto,
                                                                       @RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                                       @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit) {
        return GenericModelMapper.convertList(
                driverService.getDriversFiltered(filteredDriverGetDto, offset, limit), DriverGetDto.class);
    }

    @PutMapping(RequestsInfo.STATISTICS + RequestsInfo.FILTERED_DRIVERS)
    public ListForDto<DriverGetDto> getFilteredDriversForStatistics(@Valid @RequestBody FilteredDriverGetDto filteredDriverGetDto,
                                                                    @RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                                    @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit) {
        return GenericModelMapper.convertList(
                driverService.getDriversFilteredForStatistics(filteredDriverGetDto, offset, limit), DriverGetDto.class);
    }

    @GetMapping(RequestsInfo.ALL + RequestsInfo.PERMIT_TYPE)
    public ListForDto<PermitTypeGetDto> getAllPermitTypes() {
        return GenericModelMapper.convertList(driverService.getAllPermitTypes(), PermitTypeGetDto.class);
    }

    @GetMapping(RequestsInfo.SORTED_DRIVERS)
    public ListForDto<DriverGetDto> getSortedDriversByPermitTypeCreatedTime(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                                            @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                                                            @RequestParam(defaultValue = "active") String permitType,
                                                                            @RequestParam(defaultValue = "ASC") String sortOrder) {
        return GenericModelMapper.convertList(
                driverService.findAllSortedByPermissionUpdateCreatedDate(offset, limit, permitType, sortOrder), DriverGetDto.class);
    }


    @PostMapping(RequestsInfo.SPOTS)
    public ListForDto<DriverGetDto> getDriversBySpots(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                      @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                                      @RequestParam(defaultValue = RequestsInfo.DEFAULT_SEARCH_VALUE) String search,
                                                      @RequestBody List<Long> spots) {
        return GenericModelMapper.convertList(
                driverService.findAllByParameter(offset, limit, spots, SpecificationParameterType.SPOT, search), DriverGetDto.class);
    }
}
