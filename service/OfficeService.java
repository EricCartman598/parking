package com.epam.parking.service;

import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.LocationCreateDto;
import com.epam.parking.dto.create.ManagerCreateDto;
import com.epam.parking.dto.get.LocationGetDto;
import com.epam.parking.dto.get.SpotGetDto;
import com.epam.parking.dto.update.LocationUpdateDto;
import com.epam.parking.dto.update.SpotUpdateDto;
import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.*;
import com.epam.parking.repository.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OfficeService {

    private final OfficeRepository officeRepository;
    private final LocationService locationService;
    private final SpotService spotService;
    private final PermissionService permissionService;
    private final ManagerService managerService;
    private final PermissionTypeService permissionTypeService;

    @Transactional(readOnly = true)
    public ListForDto<Office> getAllOffices(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Office> page = officeRepository.findAll(pageable);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(readOnly = true)
    public ListForDto<LocationGetDto> getAllLocationsByOfficeId(long officeId) {
        return locationService.getLocationsByOfficeId(officeId);
    }

    @Transactional(readOnly = true)
    public Office getOfficeById(long id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Office.class));
    }

    @Transactional(readOnly = true)
    public long getCapacityById(long id) {
        return getAllLocationsByOfficeId(id).getResults()
                .stream()
                .collect(Collectors.summarizingLong(i -> locationService.getCapacityById(i.getId())))
                .getSum();
    }

    @Transactional(readOnly = true)
    public ListForDto<Permission> getPermissionsById(long id) {
        return permissionService.getByOfficeId(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Manager createManager(ManagerCreateDto managerCreateDto) {
        Office office = getOfficeById(managerCreateDto.getOfficeId());
        PermissionType permissionType = permissionTypeService.findByTitle(managerCreateDto.getTitle());
        return managerService.createManager(managerCreateDto.getEmail().toLowerCase(), permissionType, office);
    }

    @Transactional(readOnly = true)
    public LocationGetDto getLocationWithCapacity(long locationId) {
        return locationService.getLocationWithCapacity(locationId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location createLocation(LocationCreateDto locationCreateDto) {
        return locationService.createLocation(getOfficeById(locationCreateDto.getOfficeId()), locationCreateDto);
    }

    @Transactional(readOnly = true)
    public ListForDto<Permission> getAllPermissions() {
        return permissionService.getAll();
    }

    @Transactional
    public void deleteLocationById(long locationId) {
        locationService.deleteLocationById(locationId);
    }

    @Transactional
    public void deleteSpotById(long spotId) {
        locationService.deleteSpotById(spotId);
    }

    @Transactional
    public LocationGetDto updateLocationById(LocationUpdateDto locationUpdateDto, long locationId) {
        return locationService.updateLocationById(locationUpdateDto, locationId);
    }

    @Transactional
    public SpotGetDto updateSpotById(long spotId, SpotUpdateDto spotUpdateDto) {
        return locationService.getSpotDtoWithDrivers(spotService.updateSpotById(spotId, spotUpdateDto));
    }

    @Transactional
    public void deleteManagerById(long managerId) {
        managerService.deleteManagerById(managerId);
    }
}
