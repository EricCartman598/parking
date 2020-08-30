package com.epam.parking.service;

import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.PermitType;
import com.epam.parking.repository.PermitTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermitTypeService {

    private final PermitTypeRepository permitTypeRepository;

    @Transactional(readOnly = true)
    public PermitType findByTitle(String title) {
        return permitTypeRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(PermitType.class));
    }

    @Transactional(readOnly = true)
    public List<PermitType> findAll() {
        return permitTypeRepository.findAll();
    }
}
