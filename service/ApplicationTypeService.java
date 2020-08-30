package com.epam.parking.service;

import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.Application;
import com.epam.parking.model.ApplicationType;
import com.epam.parking.repository.ApplicationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ApplicationTypeService {

    private final ApplicationTypeRepository applicationTypeRepository;

    @Transactional(readOnly = true)
    public ApplicationType findByTitle(String title) {
        return applicationTypeRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(Application.class));
    }
}
