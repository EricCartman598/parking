package com.epam.parking.controller;

import com.epam.parking.common.RequestsInfo;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.LocationCreateDto;
import com.epam.parking.dto.create.ManagerCreateDto;
import com.epam.parking.dto.get.*;
import com.epam.parking.dto.update.LocationUpdateDto;
import com.epam.parking.dto.update.SpotUpdateDto;
import com.epam.parking.service.OfficeService;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(RequestsInfo.OFFICES)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OfficeController {

    private final OfficeService officeService;

    @GetMapping
    public ListForDto<OfficeGetDto> getAllOffices(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                  @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit) {
        return GenericModelMapper.convertList(officeService.getAllOffices(offset, limit), OfficeGetDto.class);
    }

    @GetMapping(RequestsInfo.OFFICE_ID + RequestsInfo.LOCATIONS)
    public ListForDto<LocationGetDto> getAllLocationsByOfficeId(@PathVariable long officeId) {
        return GenericModelMapper.convertList(officeService.getAllLocationsByOfficeId(officeId), LocationGetDto.class);
    }

    @GetMapping(RequestsInfo.OFFICE_ID + RequestsInfo.LOCATIONS + RequestsInfo.LOCATION_ID)
    public LocationGetDto getLocationWithCapacity(@PathVariable long locationId) {
        return GenericModelMapper.convertToClass(officeService.getLocationWithCapacity(locationId), LocationGetDto.class);
    }

    @PutMapping(RequestsInfo.OFFICE_ID + RequestsInfo.LOCATIONS + RequestsInfo.LOCATION_ID)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public LocationGetDto updateLocationById(@RequestBody LocationUpdateDto locationUpdateDto,
                                             @PathVariable long locationId) {
        return officeService.updateLocationById(locationUpdateDto, locationId);
    }

    @GetMapping(RequestsInfo.OFFICE_ID + RequestsInfo.CAPACITY)
    public long getCapacityByOfficeId(@PathVariable long officeId) {
        return officeService.getCapacityById(officeId);
    }

    @GetMapping(RequestsInfo.OFFICE_ID + RequestsInfo.PERMISSIONS)
    public ListForDto<PermissionGetDto> getPermissionsById(@PathVariable long officeId) {
        return GenericModelMapper.convertList(officeService.getPermissionsById(officeId), PermissionGetDto.class);
    }

    @PostMapping(RequestsInfo.OFFICE_ID + RequestsInfo.PERMISSIONS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ManagerGetDto createManager(@RequestBody ManagerCreateDto managerCreateDto) {
        return GenericModelMapper.convertToClass(officeService.createManager(managerCreateDto), ManagerGetDto.class);
    }

    @PostMapping(RequestsInfo.OFFICE_ID + RequestsInfo.LOCATIONS)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public LocationGetDto createLocation(@RequestBody LocationCreateDto locationCreateDto) {
        return GenericModelMapper.convertToClass(officeService.createLocation(locationCreateDto), LocationGetDto.class);
    }

    @GetMapping(RequestsInfo.PERMISSIONS)
    public ListForDto<PermissionGetDto> getAllPermissionsWithOffices() {
        return GenericModelMapper.convertList(officeService.getAllPermissions(), PermissionGetDto.class);
    }

    @DeleteMapping(RequestsInfo.OFFICE_ID + RequestsInfo.LOCATIONS + RequestsInfo.LOCATION_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public void deleteLocation(@PathVariable long locationId) {
        officeService.deleteLocationById(locationId);
    }

    @DeleteMapping(RequestsInfo.OFFICE_ID + RequestsInfo.LOCATIONS + RequestsInfo.LOCATION_ID + RequestsInfo.SPOT_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public void deleteSpot(@PathVariable long spotId) {
        officeService.deleteSpotById(spotId);
    }

    @PutMapping(RequestsInfo.SPOT_ID)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public SpotGetDto updateSpotById(@PathVariable long spotId, @RequestBody SpotUpdateDto spotUpdateDto) {
        return GenericModelMapper.convertToClass(officeService.updateSpotById(spotId, spotUpdateDto), SpotGetDto.class);
    }

    @DeleteMapping(RequestsInfo.MANAGER_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public void deleteManager(@PathVariable long managerId) {
        officeService.deleteManagerById(managerId);
    }
}
