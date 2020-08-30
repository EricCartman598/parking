package com.epam.parking.service;

import com.epam.parking.common.Constants;
import com.epam.parking.common.PermitTypeEnum;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.LocationCreateDto;
import com.epam.parking.dto.get.DriverGetDto;
import com.epam.parking.dto.get.LocationGetDto;
import com.epam.parking.dto.get.SpotGetDto;
import com.epam.parking.dto.update.LocationUpdateDto;
import com.epam.parking.dto.update.SpotUpdateDto;
import com.epam.parking.exception.*;
import com.epam.parking.model.*;
import com.epam.parking.repository.LocationRepository;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocationService {

    private final LocationRepository locationRepository;
    private final SpotService spotService;
    private final DriverService driverService;

    @Transactional(readOnly = true)
    public ListForDto<LocationGetDto> getLocationsByOfficeId(long officeId) {
        List<LocationGetDto> list = locationRepository.findAll()
                .stream()
                .filter(l -> l.getOffice().getId() == officeId)
                .map(location -> getLocationWithCapacity(location.getId()))
                .collect(Collectors.toList());
        return new ListForDto<>(list.size(), list);
    }

    @Transactional(readOnly = true)
    public long getCapacityById(long id) {
        return spotService.getSpotsByLocationId(id).stream()
                .collect(Collectors.summarizingLong(Spot::getCapacity))
                .getSum();
    }

    public LocationGetDto getLocationWithCapacity(long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException(Location.class));

        long occupied = getOccupiedSpots(location);
        long capacity = getCapacityById(locationId);

        LocationGetDto locationGetDto = GenericModelMapper.convertToClass(location, LocationGetDto.class);
        locationGetDto.setTotalCapacity(capacity);
        locationGetDto.setOccupied(occupied);
        locationGetDto.setAvailable(capacity - occupied);
        locationGetDto.setSpots(location.getSpots()
                .stream()
                .map(this::getSpotDtoWithDrivers)
                .collect(Collectors.toList()));
        return locationGetDto;
    }

    @Transactional
    public SpotGetDto getSpotDtoWithDrivers(Spot spot) {
        List<Driver> driversBySpot = spot.getLinkedPermitUpdates()
                .stream()
                .map(PermitUpdateHistory::getDriver)
                .collect(Collectors.toList());
        driversBySpot.forEach(driverService::setCurrentPermit);

        List<DriverGetDto> drivers = driversBySpot.stream()
                .filter(driver -> driverService.getCurrentPermit(driver).isPresent())
                .filter(driver -> driverService.getCurrentPermit(driver).get()
                        .getPermitType()
                        .getTitle()
                        .equals(PermitTypeEnum.ACTIVE.toString()))
                .map(driver -> GenericModelMapper.convertToClass(driver, DriverGetDto.class))
                .collect(Collectors.toList());

        return new SpotGetDto(spot.getId(), spot.getTitle(), spot.getCapacity(),
                drivers, spot.getLocation().getTitle(), spot.getLocation().getId());
    }

    private long getOccupiedSpots(Location location) {
        return location.getSpots()
                .stream()
                .mapToLong(spot -> spot.getLinkedPermitUpdates().size())
                .sum();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location createLocation(Office office, LocationCreateDto locationCreateDto) {
        String title = locationCreateDto.getTitle();
        Location location = new Location(title, office);

        location.setSummary(locationCreateDto.getSummary());
        location.setRealCapacity(locationCreateDto.getRealCapacity());

        List<SpotGetDto> spots = locationCreateDto.getSpots();
        long capacity = locationCreateDto.getCapacity();

        if (spots.isEmpty()) {
            location.setSpots(Collections.singletonList(new Spot(title + Constants.SPOTS, capacity, location, null)));
        } else {
            long spotCapacity = spots.stream()
                    .collect(Collectors.summarizingLong(SpotGetDto::getCapacity))
                    .getSum();

            if (spotCapacity != capacity) {
                throw new LocationSpotCapacityIsNotEqualLocationCapacityException(Location.class);
            }

            List<Spot> spotList = spots.stream()
                    .map(s -> new Spot(s.getTitle(), s.getCapacity(), location, null))
                    .collect(Collectors.toList());

            location.setSpots(spotList);
        }

        if (locationCreateDto.getFromDate() != null || locationCreateDto.getUntilDate() != null) {
            setLocationPeriod(locationCreateDto.getFromDate(), locationCreateDto.getUntilDate(), location);
        }

        return locationRepository.save(location);
    }

    private void setLocationPeriod(Long startOfPeriod, Long endOfPeriod, Location location) {
        if (startOfPeriod != null && endOfPeriod != null) {
            Date fromDate = GenericModelMapper.convertToClass(startOfPeriod, Date.class);
            Date untilDate = GenericModelMapper.convertToClass(endOfPeriod, Date.class);

            if (fromDate.after(untilDate) || untilDate.before(new Date())) {
                throw new WrongDatePeriodException(Location.class);
            }
            location.setFromDate(fromDate);
            location.setUntilDate(untilDate);

        } else if (startOfPeriod != null) {
            Date fromDate = GenericModelMapper.convertToClass(startOfPeriod, Date.class);
            location.setFromDate(fromDate);
        } else {
            Date untilDate = GenericModelMapper.convertToClass(endOfPeriod, Date.class);
            if (untilDate.before(new Date())) {
                throw new WrongDatePeriodException(Location.class);
            }
            location.setUntilDate(untilDate);
        }
    }

    @Transactional(readOnly = true)
    public Location findById(long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Location.class));
    }

    @Transactional
    public LocationGetDto updateLocationById(LocationUpdateDto locationUpdateDto, long locationId) {
        Location location = GenericModelMapper.convertToClass(locationUpdateDto, Location.class);
        Location foundLocation = findById(locationId);

        if (location.getTitle() != null) {
            foundLocation.setTitle(location.getTitle());
        }

        if (location.getSummary() != null) {
            foundLocation.setSummary(location.getSummary());
        }

        if (location.getRealCapacity() != null) {
            foundLocation.setRealCapacity(location.getRealCapacity());
        }

        if (location.getFromDate() != null || location.getUntilDate() != null) {
            if (location.getFromDate() != null && locationUpdateDto.getFromDate() == 0) {
                locationUpdateDto.setFromDate(null);
                foundLocation.setFromDate(null);
            }

            if (location.getUntilDate() != null && locationUpdateDto.getUntilDate() == 0) {
                locationUpdateDto.setUntilDate(null);
                foundLocation.setUntilDate(null);
            }

            if (locationUpdateDto.getFromDate() != null || locationUpdateDto.getUntilDate() != null) {
                setLocationPeriod(locationUpdateDto.getFromDate(), locationUpdateDto.getUntilDate(), foundLocation);
            }
        }

        if (locationUpdateDto.getSpots() != null) {
            List<SpotUpdateDto> spots = locationUpdateDto.getSpots();

            for (SpotUpdateDto spotUpdateDto : spots) {
                Spot spot = GenericModelMapper.convertToClass(spotUpdateDto, Spot.class);
                long spotId = spot.getId();

                if (spotId == 0) {
                    spot.setLocation(foundLocation);
                    foundLocation.getSpots().add(spot);
                } else {
                    Spot foundSpot = foundLocation.getSpots().stream()
                            .filter(s -> s.getId() == spotId)
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(Spot.class));

                    long capacity = spot.getCapacity();

                    if (foundSpot.getLinkedPermitUpdates() != null && capacity < foundSpot.getLinkedPermitUpdates().size()) {
                        throw new SpotCapacityCanNotBeLessThanOccupiedException(Spot.class);
                    }

                    foundSpot.setCapacity(capacity);

                    if (spot.getTitle() != null) {
                        foundSpot.setTitle(spot.getTitle());
                    }
                }
            }
        }

        return GenericModelMapper.convertToClass(locationRepository.save(foundLocation), LocationGetDto.class);
    }

    public void deleteLocationById(long locationId) {
        if (getLocationWithCapacity(locationId).getOccupied() != 0) {
            throw new OccupiedSpotsAreNotEmptyException(Location.class);
        }
        locationRepository.deleteById(locationId);
    }

    public void deleteSpotById(long spotId) {
        Location location = spotService.getSpotById(spotId).getLocation();

        boolean isEmpty = location.getSpots().stream()
                .filter(s -> s.getId() == spotId)
                .anyMatch(sp -> sp.getLinkedPermitUpdates().size() == 0);

        if (!isEmpty) {
            throw new SpotIsNotEmptyException(Spot.class);
        }

        location.getSpots().removeIf(s -> s.getId() == spotId);
        spotService.deleteSpotById(spotId);
    }
}
