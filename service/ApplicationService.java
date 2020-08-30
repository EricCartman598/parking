package com.epam.parking.service;

import com.epam.parking.common.PermitTypeEnum;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.ApplicationCreateDto;
import com.epam.parking.dto.get.FilteredApplicationGetDto;
import com.epam.parking.dto.update.ApplicationTypeUpdateDto;
import com.epam.parking.exception.ExistingEmailException;
import com.epam.parking.exception.ForbiddenException;
import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.*;
import com.epam.parking.repository.ApplicationRepository;
import com.epam.parking.repository.LocationRepository;
import com.epam.parking.service.specification.ApplicationSpecification;
import com.epam.parking.service.specification.BaseSpecification;
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

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationService {

    private static final String APPLICATION_TYPE_NEW = "new";
    private static final String APPLICATION_TYPE_APPROVED = "approved";
    private final ApplicationRepository applicationRepository;
    private final LocationRepository locationRepository;
    private final PermitTypeService permitTypeService;

    private final DriverService driverService;
    private final ApplicationTypeService applicationTypeService;
    private final NotificationService notificationService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Application createApplication(ApplicationCreateDto applicationDto) {
        Driver driver = driverService.findByEmail(applicationDto.getEmail())
                .map(d -> {
                    if (d.getApplications().stream().anyMatch(a -> !a.isDeleted())) {
                        throw new ExistingEmailException();
                    }
                    d.setDeleted(false);
                    return d;
                })
                .orElseGet(() -> new Driver(applicationDto.getEmail(), applicationDto.getPhoneNumber(), true));

        driver.setTrack(applicationDto.getTrack());
        driver.setLevel(applicationDto.getLevel());
        driver.setOffice(applicationDto.getOffice());
        driver.setParkingInOffice(applicationDto.getParkingInOffice());
        driver.setStartedWork(GenericModelMapper.convertToClass(applicationDto.getStartedWork(), Date.class));
        driver.setVehicles(driverService.listVehicleCreateDtoToListVehicle(applicationDto.getVehicles(), driver));
        Application application = new Application(driver, applicationTypeService.findByTitle(APPLICATION_TYPE_NEW), new Date());
        Optional<Location> desiredLocationOpt = locationRepository.findByTitle(applicationDto.getDesiredLocation());
        desiredLocationOpt.ifPresent(application::setDesiredLocation);
        return applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public ListForDto<Application> findAll(int offset, int limit) {
        Pageable pageable = receiveFilteredPage(offset, limit);
        Page<Application> page = applicationRepository.findAll(
                where(BaseSpecification.hasNotDeleted()), pageable);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    @Transactional
    public void deleteApplication(long id) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new NotFoundException(Application.class));
        Driver driver = Optional.ofNullable(application.getDriver()).orElseThrow(() -> new NotFoundException(Driver.class));
        driver.setApplier(false);
        application.setDeleted(true);
    }

    @Transactional(readOnly = true)
    public Application findById(long id) {
        return applicationRepository.findById(id)
                .filter(application -> !application.isDeleted())
                .orElseThrow(() -> new NotFoundException(Application.class));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApplicationType updateTypeByTitle(int id, ApplicationTypeUpdateDto typeDto) throws MessagingException {
        Application application = findById(id);
        application.setApplicationType(applicationTypeService.findByTitle(typeDto.getTitle()));
        application = applicationRepository.save(application);

        if (application.getApplicationType().getTitle().equals(APPLICATION_TYPE_APPROVED)) {
            driverService.changeIsApplierFlag(application.getDriver());
        }

        notificationService.sendNotification(application.getDriver().getEmail(), typeDto.getTitle(),typeDto.getComment());
        return application.getApplicationType();
    }

    @Transactional(readOnly = true)
    public ListForDto<Application> findAllByKeyWord(int offset, int limit, String search) {
        if (AuthorityOperations.isForbiddenRequestByUser(search)){
            throw new ForbiddenException();
        }

        search = analyzeSearchKey(search);
        Pageable pageable = receiveFilteredPage(offset, limit);
        Page<Application> page = applicationRepository.findAll(
                where(ApplicationSpecification.likeEmailOrNameOrLastName(search.toLowerCase()))
                        .or(ApplicationSpecification.likeLicensePlate(search.toUpperCase()))
                        .or(ApplicationSpecification.likeType(search.toLowerCase()))
                        .and(BaseSpecification.hasNotDeleted()), pageable);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }


    @Transactional(readOnly = true)
    public long getNewApplicationCount() {

        return applicationRepository.findAll(
                where(ApplicationSpecification.likeType(APPLICATION_TYPE_NEW))
                        .and(BaseSpecification.hasNotDeleted())).size();
    }

    @Transactional(readOnly = true)
    public ListForDto<Application> filterApplications(int offset, int limit, String search, FilteredApplicationGetDto applicationGetDto) {
        Specification<Application> specification = getApplicationSpecification(applicationGetDto, search);
        Pageable pageable = receiveFilteredPage(offset, limit);
        Page<Application> page = applicationRepository.findAll(specification, pageable);
        return new ListForDto<>(page.getTotalElements(), page.getContent());
    }

    private Specification<Application> getApplicationSpecification(FilteredApplicationGetDto dto, String search) {
        SpecificationWrapper<Application> specification = new SpecificationWrapper<>(BaseSpecification.hasNotDeleted());
        Optional<FilteredApplicationGetDto> optionalDto = Optional.of(dto);
        SpecificationWrapper<Application> specificationWrapper = new SpecificationWrapper<>
                (ApplicationSpecification.equalApplicationType(dto.getApplicationTypeTitle().get(0)));

        optionalDto.map(FilteredApplicationGetDto::getDateOfReceiptFrom)
                .ifPresent(s -> specification.and(ApplicationSpecification.receiptAfter(convertDate(s))));

        optionalDto.map(FilteredApplicationGetDto::getDateOfReceiptTo)
                .ifPresent(s -> specification.and(ApplicationSpecification.receiptBefore(convertDate(s))));

        optionalDto.map(FilteredApplicationGetDto::getDateOfUpdateFrom)
                .ifPresent(s -> specification.and(ApplicationSpecification.updateAfter(convertDate(s))));

        optionalDto.map(FilteredApplicationGetDto::getDateOfUpdateTo)
                .ifPresent(s -> specification.and(ApplicationSpecification.updateBefore(convertDate(s))));

        optionalDto.map(FilteredApplicationGetDto::getOffice)
                .ifPresent(s -> specification.and(ApplicationSpecification.equalOffice(s)));

        optionalDto.map(FilteredApplicationGetDto::getApplicationTypeTitle)
                .ifPresent(s -> s.forEach(f -> specificationWrapper.or(ApplicationSpecification.equalApplicationType(f))));

        search = analyzeSearchKey(search);

        if (!search.isEmpty()) {
            specificationWrapper.and(ApplicationSpecification.likeEmailOrNameOrLastName(search.toLowerCase())
                    .or(ApplicationSpecification.likeLicensePlate(search.toUpperCase())));
        }

        specification.and(specificationWrapper.getSpecification());

        return specification.getSpecification();
    }

    private Date convertDate(Double value) {
        return GenericModelMapper.convertToClass(value, Date.class);
    }

    private Pageable receiveFilteredPage(int offset, int limit) {
        return PageRequest.of(offset / limit, limit, Sort.by("applicationType").and(Sort.by("createdAt")));
    }

    private String analyzeSearchKey(String key) {
        key = key.trim().replaceAll(" ", "_");
        return key;
    }
}
