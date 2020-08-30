package com.epam.parking.service;

import com.epam.parking.common.Constants;
import com.epam.parking.common.PermitTypeEnum;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.DriverCreateDto;
import com.epam.parking.dto.create.PermitUpdateCreateDto;
import com.epam.parking.dto.create.VehicleCreateDto;
import com.epam.parking.dto.get.FilteredDriverGetDto;
import com.epam.parking.dto.get.SpotGetDto;
import com.epam.parking.dto.update.BatchPermitUpdateUpdateDto;
import com.epam.parking.dto.update.DriverToPermitUpdateUpdateDto;
import com.epam.parking.dto.update.DriverUpdateDto;
import com.epam.parking.dto.update.PermitUpdateUpdateDto;
import com.epam.parking.exception.*;
import com.epam.parking.model.*;
import com.epam.parking.repository.DriverRepository;
import com.epam.parking.service.specification.BaseSpecification;
import com.epam.parking.service.specification.DriverSpecification;
import com.epam.parking.service.specification.SpecificationParameterType;
import com.epam.parking.service.specification.SpecificationWrapper;
import com.epam.parking.util.AuthorityOperations;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverService {

    private final DriverRepository driverRepository;
    private final PermitTypeService permitTypeService;
    private final VehicleBrandService vehicleBrandService;
    private final VehicleModelService vehicleModelService;
    private final VehicleTypeService vehicleTypeService;
    private final LeaveReasonService leaveReasonService;
    private final SpotService spotService;

    @Transactional(readOnly = true)
    Optional<Driver> findByEmail(String email) {
        Optional<Driver> driverOpt = driverRepository.findByEmail(email);
        driverOpt.ifPresent(this::setCurrentPermit);
        return driverOpt;
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> findAll(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Driver> page = driverRepository.findAll(BaseSpecification.hasNotDeleted(), pageable);
        page.getContent().forEach(this::setCurrentPermit);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(readOnly = true)
    public Boolean checkByEmail(String email) {
        return (findByEmail(email)
                .orElseThrow(() -> new NotFoundException(Driver.class))) != null;
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> findAllSortedByPermissionUpdateCreatedDate(int offset, int limit, String permitType, String sortOrder) {
        Specification specification = DriverSpecification.equalPermitType(permitType);
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.fromString(sortOrder), Constants.CREATED_AT));
        Page<Driver> page = driverRepository.findAll(specification, pageable);
        page.getContent().forEach(this::setCurrentPermit);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Driver save(DriverCreateDto driverDto) {
        Driver driver = new Driver(driverDto.getEmail().toLowerCase(), driverDto.getPhoneNumber(), false);
        PermitUpdateHistory permitUpdateHistory = new PermitUpdateHistory(driver, permitTypeService.findByTitle(driverDto.getPermitTypeTitle()));
        permitUpdateHistory.setUpdatedAt(new Date());
        permitUpdateHistory.setCurrent(true);
        driver.setPermitUpdatesHistory(Collections.singletonList(permitUpdateHistory));
        if (driverDto.getLeaveReason() != null) {
            permitUpdateHistory.setLeaveReason(leaveReasonService.findByTitle(driverDto.getLeaveReason()));
        }
        if (driverDto.getVehicles() != null) {
            List<Vehicle> vehicles = listVehicleCreateDtoToListVehicle(driverDto.getVehicles(), driver);
            driver.setVehicles(vehicles);
        }
        driver.setCurrentPermit(permitUpdateHistory);
        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public Driver findById(long id) {
        Driver found = driverRepository.findById(id)
                .filter(driver -> !driver.isDeleted())
                .orElseThrow(() -> new NotFoundException(Driver.class));

        if(AuthorityOperations.isForbiddenRequestByUser(found.getEmail())){
            throw new ForbiddenException();
        }

        setCurrentPermit(found);
        return found;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Driver updateById(long id, DriverUpdateDto driver) {
        Date startedWork = prepareStartedWork(driver.getStartedWork());
        driverRepository.updateViaQuery(id, driver.getEmail(), driver.getPhoneNumber(),
                startedWork, driver.getTrack(), driver.getLevel(), driver.getParkingInOffice(), driver.getOffice());
        Driver updatedDriver = driverRepository.getOne(id);
        setCurrentPermit(updatedDriver);
        return updatedDriver;
    }

    @Transactional
    public void deleteById(long id) {
        Driver driver = findById(id);
        if (getCurrentPermit(driver).isPresent() && !getCurrentPermit(driver).get().getSpots().isEmpty()) {
            throw new DriverOccupiedSpotsException(Driver.class);
        }
        driverRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ListForDto<Vehicle> findAllCarsById(long id) {
        List<Vehicle> vehicles = findById(id).getVehicles().stream()
                .filter(vehicle -> !vehicle.isDeleted())
                .collect(Collectors.toList());
        return new ListForDto<>(vehicles.size(), vehicles);
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> findAllByEmailOrLicensePlate(int offset, int limit, String search) {
        search = search.trim().replaceAll(" ", "_");
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Driver> page = driverRepository.findAll(where(DriverSpecification.likeEmail(search.toLowerCase()))
                .or(DriverSpecification.likeLicensePlate(search.toUpperCase()))
                .and(BaseSpecification.hasNotDeleted()), pageable);
        page.getContent().forEach(this::setCurrentPermit);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional
    public void changeIsApplierFlag(Driver driver) {
        driver.setApplier(!driver.isApplier());
        addPermitUpdate(driver.getId(),
                new PermitUpdateCreateDto(PermitTypeEnum.IN_LINE.toString(), null, null));
        driverRepository.save(driver);
    }

    @Transactional
    public Driver addPermitUpdate(long id, PermitUpdateCreateDto permitUpdateDto) {
        Driver driver = findById(id);
        if (driver == null) {
            throw new DriverNotFoundException();
        }
        setDriverNewPermit(driver, permitUpdateDto);
        return driver;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Driver updatePermitUpdate(long driverId, PermitUpdateUpdateDto permitUpdateUpdateDto) {
        Driver driver = findById(driverId);
        PermitUpdateHistory foundPermitUpdate = driver.getPermitUpdatesHistory().stream()
                .filter(p -> p.getId() == permitUpdateUpdateDto.getId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException(PermitUpdateHistory.class));

        if (permitUpdateUpdateDto.getUpdatedAt() != null) {
            Date updatedAt = GenericModelMapper.convertToClass(permitUpdateUpdateDto.getUpdatedAt(), Date.class);
            if (updatedAt.before(new Date())) {
                foundPermitUpdate.setUpdatedAt(updatedAt);
            } else {
                throw new InvalidDateException();
            }
        }

        List<SpotGetDto> spots = Optional.ofNullable(permitUpdateUpdateDto.getSpotIds())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(spotService::getSpotById)
                .map(s -> GenericModelMapper.convertToClass(s, SpotGetDto.class))
                .collect(Collectors.toList());

        if (!spots.isEmpty()) {
            removeLinkedSpots(foundPermitUpdate, foundPermitUpdate.getSpots());
        }

        PermitUpdateCreateDto permitUpdateDto = new PermitUpdateCreateDto(permitUpdateUpdateDto.getPermitType(),
                permitUpdateUpdateDto.getLeaveReason(), spots);
        setPermitUpdate(permitUpdateDto, foundPermitUpdate);
        setCurrentPermit(driver);
        return driver;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ListForDto<Driver> updateBatchPermitUpdateList(List<BatchPermitUpdateUpdateDto> batchPermitUpdateUpdateDtos) {
        List<Driver> drivers = new ArrayList<>();
        for (BatchPermitUpdateUpdateDto batchPermitUpdateUpdateDto : batchPermitUpdateUpdateDtos) {
            drivers.addAll(updatePermitUpdate(batchPermitUpdateUpdateDto));
        }
        return new ListForDto<>(drivers.size(), drivers);
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> getDriversFilteredByPermitType(int offset, int limit, String permitType, String search) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Constants.EMAIL));
        Page<Driver> page = driverRepository.findAll(where(DriverSpecification.likeEmail(search.toLowerCase()))
                .or(DriverSpecification.likeLicensePlate(search.toUpperCase()))
                .and(BaseSpecification.hasNotDeleted())
                .and(DriverSpecification.equalPermitType(permitType)), pageable);
        page.getContent().forEach(this::setCurrentPermit);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> findAllByParameter(int offset, int limit, List<Long> parameters,
                                                 SpecificationParameterType specificationParameterType, String search) {
        if (parameters == null || parameters.isEmpty()) {
            return new ListForDto<>(0, Collections.emptyList());
        }
        Specification<Driver> specification = getDriverSpecification(parameters, specificationParameterType, search);

        return getDriverListForDto(offset, limit, specification);
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> getDriversFiltered(FilteredDriverGetDto filteredDriverGetDto, int offset, int limit) {
        List<Specification> specifications = new ArrayList<>();
        List<Specification> dateSpecificationsList = new ArrayList<>();

        setConstraintByPermitTypeAndDate(dateSpecificationsList, PermitTypeEnum.IN_LINE.toString(),
                filteredDriverGetDto.getAppliedFrom(),
                filteredDriverGetDto.getAppliedUntil());

        setConstraintByPermitTypeAndDate(dateSpecificationsList, PermitTypeEnum.ACTIVE.toString(),
                filteredDriverGetDto.getIssuedFrom(),
                filteredDriverGetDto.getIssuedUntil());

        setConstraintByPermitTypeAndDate(dateSpecificationsList, PermitTypeEnum.INACTIVE.toString(),
                filteredDriverGetDto.getLeftFrom(),
                filteredDriverGetDto.getLeftUntil());

        if (!dateSpecificationsList.isEmpty()) {
            Specification<Driver> dateSpec = dateSpecificationsList.get(0);
            for (Specification<Driver> spec : dateSpecificationsList) {
                dateSpec = dateSpec.or(spec);
            }
            specifications.add(dateSpec);
        }

        String leaveReason = filteredDriverGetDto.getLeaveReason();
        if (leaveReason != null) {
            specifications.add(DriverSpecification.hasLeaveReasonCurrentPermit(leaveReason));
        }

        setConstraintByVehicleBrands(specifications, filteredDriverGetDto.getVehicleBrands());
        setConstraintByVehicleModels(specifications, filteredDriverGetDto.getVehicleModels());
        setConstraintByLocations(specifications, filteredDriverGetDto.getLocations());

        String permitType = filteredDriverGetDto.getPermitType();
        if (permitType != null) {
            specifications.add(DriverSpecification.equalPermitType(permitType));
        }

        return getDriverListForDto(offset, limit, getSpecificationForDriver(specifications));
    }

    @Transactional(readOnly = true)
    public ListForDto<Driver> getDriversFilteredForStatistics(FilteredDriverGetDto filteredDriverGetDto, int offset, int limit) {
        List<Specification> specifications = new ArrayList<>();
        specifications.add(DriverSpecification.hasPermitTypeInHistory(filteredDriverGetDto.getPermitType()));
        switch (PermitTypeEnum.fromString(filteredDriverGetDto.getPermitType())) {
            case ACTIVE:
                setConstraintByDateInPermitHistory(specifications, filteredDriverGetDto.getIssuedFrom(),
                        filteredDriverGetDto.getIssuedUntil());
                break;
            case IN_LINE:
                setConstraintByDateInPermitHistory(specifications, filteredDriverGetDto.getAppliedFrom(),
                        filteredDriverGetDto.getAppliedUntil());
                break;
            case INACTIVE:
                setConstraintByDateInPermitHistory(specifications, filteredDriverGetDto.getLeftFrom(),
                        filteredDriverGetDto.getLeftUntil());
                String leaveReason = filteredDriverGetDto.getLeaveReason();
                if (leaveReason != null) {
                    Specification<Driver> leaveReasonSpecification = DriverSpecification.hasLeaveReasonInPermitHistory(leaveReason);
                    specifications.add(leaveReasonSpecification);
                }
                break;
        }
        return getDriverListForDto(offset, limit, getSpecificationForDriver(specifications));
    }

    @Transactional(readOnly = true)
    public ListForDto<PermitType> getAllPermitTypes() {
        List<PermitType> permitTypes = permitTypeService.findAll();
        return new ListForDto<>(permitTypes.size(), permitTypes);
    }

    private Specification<Driver> getDriverSpecification(List<Long> parameters,
                                                         SpecificationParameterType specificationParameterType, String search) {
        SpecificationWrapper<Driver> specification = new SpecificationWrapper<>(BaseSpecification.hasNotDeleted());
        specification.and(DriverSpecification.likeEmail(search.toLowerCase())
                .or(DriverSpecification.likeLicensePlate(search.toUpperCase())));

        SpecificationWrapper<Driver> hasSpecialSpecification = null;
        switch (specificationParameterType) {
            case LOCATION:
                hasSpecialSpecification = new SpecificationWrapper<>(DriverSpecification.equalLocation(parameters.get(0)));
                for (Long locationId : parameters) {
                    hasSpecialSpecification.or(DriverSpecification.equalLocation(locationId));
                }
                break;
            case SPOT:
                hasSpecialSpecification = new SpecificationWrapper<>(DriverSpecification.equalSpot(parameters.get(0)));
                for (Long spotId : parameters) {
                    hasSpecialSpecification.or(DriverSpecification.equalSpot(spotId));
                }
                break;
        }

        specification.and(hasSpecialSpecification.getSpecification());
        specification.and(DriverSpecification.equalPermitType(PermitTypeEnum.ACTIVE.toString()));
        return specification.getSpecification();
    }

    public List<Vehicle> listVehicleCreateDtoToListVehicle(List<VehicleCreateDto> list, Driver driver) {
        List<Vehicle> result = new ArrayList<>();
        for (VehicleCreateDto vehicleDto : list) {
            Vehicle vehicle = GenericModelMapper.convertToClass(vehicleDto, Vehicle.class);
            vehicle.setVehicleBrand(vehicleBrandService.findByTitle(vehicleDto.getVehicleBrandTitle()));
            if (vehicleDto.getVehicleModelTitle() != null) {
                vehicle.setVehicleModel(vehicleModelService
                        .findByTitle(vehicleDto.getVehicleModelTitle(), vehicleDto.getVehicleBrandTitle()));
            }
            vehicle.setVehicleType(vehicleTypeService.findByTitle(vehicleDto.getVehicleTypeTitle()));
            vehicle.setDriver(driver);
            result.add(vehicle);
        }
        return result;
    }

    private Date prepareStartedWork(Long longStartedWork) {
        Date startedWork;
        if (longStartedWork != null) {
            startedWork = GenericModelMapper.convertToClass(longStartedWork, Date.class);
        } else {
            startedWork = new Date(0);
        }
        return startedWork;
    }

    public void setCurrentPermit(Driver driver) {
        if (getCurrentPermit(driver).isPresent()) {
            driver.setCurrentPermit(getCurrentPermit(driver).get());
        }
    }

    public Optional<PermitUpdateHistory> getCurrentPermit(Driver driver) {
        if (driver.getPermitUpdatesHistory() != null) {
            return Optional.of(driver.getPermitUpdatesHistory().stream()
                    .filter(PermitUpdateHistory::isCurrent)
                    .findFirst()).orElse(Optional.empty());
        }
        return Optional.empty();
    }

    private void setDriverNewPermit(Driver driver, PermitUpdateCreateDto permitUpdateDto) {
        PermitUpdateHistory newCurrentPermitUpdate = new PermitUpdateHistory(driver, permitTypeService.findByTitle(permitUpdateDto.getPermitType()));
        setPermitUpdate(permitUpdateDto, newCurrentPermitUpdate);
        newCurrentPermitUpdate.setUpdatedAt(new Date());

        if (driver.getPermitUpdatesHistory() != null && !driver.getPermitUpdatesHistory().isEmpty()) {
            for (PermitUpdateHistory history : driver.getPermitUpdatesHistory()) {
                history.setCurrent(false);
            }
        }
        newCurrentPermitUpdate.setCurrent(true);
        driver.setCurrentPermit(newCurrentPermitUpdate);
        driver.getPermitUpdatesHistory().add(newCurrentPermitUpdate);
    }

    private void removeLinkedSpots(PermitUpdateHistory permitUpdate, List<Spot> spots) {
        for (Spot spot : spots) {
            spotService.getSpotById(spot.getId())
                    .getLinkedPermitUpdates()
                    .remove(permitUpdate);
        }
        if (permitUpdate.getSpots() != null) {
            permitUpdate.getSpots().clear();
        }
    }

    private void setSpots(List<SpotGetDto> spots, PermitUpdateHistory foundPermitUpdate) {
        if (spots != null && !spots.isEmpty()) {
            for (SpotGetDto spot : spots) {
                Spot foundSpot = spotService.getSpotById(spot.getId());
                if (foundSpot.getLinkedPermitUpdates().size() + 1 <= foundSpot.getCapacity()) {
                    if (foundPermitUpdate.getSpots() != null) {
                        foundSpot.getLinkedPermitUpdates().add(foundPermitUpdate);
                        foundPermitUpdate.getSpots().add(foundSpot);
                    } else {
                        foundPermitUpdate.setSpots(new ArrayList<>(Collections.singletonList(foundSpot)));
                    }
                } else {
                    throw new SpotIsFullException(Spot.class);
                }
            }
        }
    }

    private void setPermitUpdate(PermitUpdateCreateDto permitUpdateDto, PermitUpdateHistory foundPermitUpdate) {
        if (permitUpdateDto.getLeaveReason() != null) {
            foundPermitUpdate.setLeaveReason(leaveReasonService.findByTitle
                    (permitUpdateDto.getLeaveReason()));
        }

        if (permitUpdateDto.getPermitType() != null) {
            if (foundPermitUpdate.getPermitType() != null
                    && foundPermitUpdate.getPermitType().getTitle().equals(PermitTypeEnum.ACTIVE.getText())
                    && !permitUpdateDto.getPermitType().equals(PermitTypeEnum.ACTIVE.getText())) {
                List<Spot> spots = foundPermitUpdate.getSpots();
                removeLinkedSpots(foundPermitUpdate, spots);
            }

            foundPermitUpdate.setPermitType(permitTypeService.findByTitle
                    (permitUpdateDto.getPermitType()));
        }
        setSpots(permitUpdateDto.getSpots(), foundPermitUpdate);
    }

    private List<Driver> updatePermitUpdate(BatchPermitUpdateUpdateDto batchPermitUpdateUpdateDto) {
        List<DriverToPermitUpdateUpdateDto> driverAndPermitUpdateIds = batchPermitUpdateUpdateDto.getDriverAndPermitUpdateId();
        PermitUpdateUpdateDto permitUpdateUpdateDto = GenericModelMapper
                .convertToClass(batchPermitUpdateUpdateDto.getPermitUpdateUpdateBaseDto(), PermitUpdateUpdateDto.class);
        List<Driver> drivers = new ArrayList<>();
        for (DriverToPermitUpdateUpdateDto driverAndPermitUpdateId : driverAndPermitUpdateIds) {
            permitUpdateUpdateDto.setId(driverAndPermitUpdateId.getPermitUpdateId());
            drivers.add(updatePermitUpdate(driverAndPermitUpdateId.getDriverId(), permitUpdateUpdateDto));
        }
        return drivers;
    }

    private void setConstraintByPermitTypeAndDate(List<Specification> specifications, String permitType, Double dateFrom, Double dateTo) {
        List<Specification> permitAndDateSpecification = new ArrayList<>();
        if (permitType != null) {
            permitAndDateSpecification.add(DriverSpecification.equalPermitType(permitType));
        }
        setConstraintByDateInCurrentPermit(permitAndDateSpecification, dateFrom, dateTo);
        if (!permitAndDateSpecification.isEmpty()) {
            Specification<Driver> spec = permitAndDateSpecification.get(0);
            for (Specification<Driver> s : permitAndDateSpecification) {
                spec = spec.and(s);
            }
            specifications.add(spec);
        }
    }

    private void setConstraintByDateInCurrentPermit(List<Specification> specifications, Double dateFrom, Double dateTo) {
        if (dateFrom != null) {
            specifications.add(DriverSpecification
                    .gotCurrentPermitAfter(GenericModelMapper.convertToClass(dateFrom, Date.class)));
        }
        if (dateTo != null) {
            specifications.add(DriverSpecification
                    .gotCurrentPermitBefore(GenericModelMapper.convertToClass(dateTo, Date.class)));
        }
    }

    private void setConstraintByDateInPermitHistory(List<Specification> specifications, Double dateFrom, Double dateTo) {
        if (dateFrom != null) {
            specifications.add(DriverSpecification
                    .gotPermitInHistoryAfter(GenericModelMapper.convertToClass(dateFrom, Date.class)));
        }
        if (dateTo != null) {
            specifications.add(DriverSpecification
                    .gotPermitInHistoryBefore(GenericModelMapper.convertToClass(dateTo, Date.class)));
        }
    }

    private void setConstraintByVehicleBrands(List<Specification> specifications, List<String> vehicleBrands) {
        if (vehicleBrands != null && !vehicleBrands.isEmpty()) {
            Specification<Driver> hasVehicleBrandSpecification = DriverSpecification.hasVehicleBrand(vehicleBrands.get(0));
            for (String brand : vehicleBrands) {
                hasVehicleBrandSpecification = hasVehicleBrandSpecification.or(DriverSpecification.hasVehicleBrand(brand));
            }
            specifications.add(hasVehicleBrandSpecification);
        }
    }

    private void setConstraintByVehicleModels(List<Specification> specifications, List<String> vehicleModels) {
        if (vehicleModels != null && !vehicleModels.isEmpty()) {
            Specification<Driver> hasVehicleModelSpecification = DriverSpecification.hasVehicleModel(vehicleModels.get(0));
            for (String model : vehicleModels) {
                hasVehicleModelSpecification = hasVehicleModelSpecification.or(DriverSpecification.hasVehicleModel(model));
            }
            specifications.add(hasVehicleModelSpecification);
        }
    }

    private void setConstraintByLocations(List<Specification> specifications, List<String> locations) {
        if (locations != null && !locations.isEmpty()) {
            Specification<Driver> hasLocationSpecification = DriverSpecification.equalLocation(locations.get(0));
            for (String location : locations) {
                hasLocationSpecification = hasLocationSpecification.or(DriverSpecification.equalLocation(location));
            }
            specifications.add(hasLocationSpecification);
        }
    }

    private ListForDto<Driver> getDriverListForDto(int offset, int limit, Specification<Driver> specification) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Constants.EMAIL));
        Page<Driver> page = driverRepository.findAll(specification, pageable);
        page.getContent().forEach(this::setCurrentPermit);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    private Specification<Driver> getSpecificationForDriver(List<Specification> specificationList) {
        Specification<Driver> specification = BaseSpecification.hasNotDeleted();
        for (Specification s : specificationList) {
            specification = specification.and(s);
        }
        return specification;
    }
}