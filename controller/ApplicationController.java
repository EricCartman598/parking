package com.epam.parking.controller;

import com.epam.parking.common.RequestsInfo;
import com.epam.parking.dto.ListForDto;
import com.epam.parking.dto.create.ApplicationCreateDto;
import com.epam.parking.dto.get.ApplicationGetDto;
import com.epam.parking.dto.get.ApplicationTypeGetDto;
import com.epam.parking.dto.get.FilteredApplicationGetDto;
import com.epam.parking.dto.update.ApplicationTypeUpdateDto;
import com.epam.parking.service.ApplicationService;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@RequestMapping(RequestsInfo.APPLICATIONS)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationGetDto createNewApplication(@Valid @RequestBody ApplicationCreateDto applicationDto) {
        return GenericModelMapper.convertToClass(applicationService.createApplication(applicationDto),
                ApplicationGetDto.class);
    }

    @GetMapping
    public ListForDto<ApplicationGetDto> getApplicationsByKeyWord(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                                  @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                                                  @RequestParam(defaultValue = RequestsInfo.DEFAULT_SEARCH_VALUE) String search) {
        return GenericModelMapper.convertList(
                applicationService.findAllByKeyWord(offset, limit, search), ApplicationGetDto.class);
    }

    @GetMapping(RequestsInfo.ALL)
    public ListForDto<ApplicationGetDto> getAllApplications(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                            @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit) {
        return GenericModelMapper.convertList(
                applicationService.findAll(offset, limit), ApplicationGetDto.class);
    }

    @GetMapping(RequestsInfo.NEW_APPLICATIONS_COUNT)
    public long getNewApplicationCount() {
        return applicationService.getNewApplicationCount();
    }

    @DeleteMapping(RequestsInfo.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public void deleteApplication(@PathVariable long id) {
        applicationService.deleteApplication(id);
    }

    @GetMapping(RequestsInfo.ID)
    @ResponseStatus(HttpStatus.FOUND)
    public ApplicationGetDto getApplicationById(@PathVariable long id) {
        return GenericModelMapper.convertToClass(applicationService.findById(id),
                ApplicationGetDto.class);
    }

    @PutMapping(RequestsInfo.ID + RequestsInfo.APPLICATION_TYPE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ApplicationTypeGetDto updateType(@PathVariable int id, @Valid @RequestBody ApplicationTypeUpdateDto typeDto)
            throws MessagingException {
        return GenericModelMapper.convertToClass(
                applicationService.updateTypeByTitle(id, typeDto), ApplicationTypeGetDto.class);
    }

    @PutMapping(RequestsInfo.FILTERED_APPLICATIONS)
    public ListForDto<ApplicationGetDto> filterApplications(@RequestParam(defaultValue = RequestsInfo.DEFAULT_OFFSET) int offset,
                                                            @RequestParam(defaultValue = RequestsInfo.DEFAULT_LIMIT) int limit,
                                                            @RequestParam(defaultValue = RequestsInfo.DEFAULT_SEARCH_VALUE) String search,
                                                            @Valid @RequestBody FilteredApplicationGetDto applicationGetDto) {
        return GenericModelMapper.convertList(
                applicationService.filterApplications(offset, limit, search, applicationGetDto), ApplicationGetDto.class);
    }
}