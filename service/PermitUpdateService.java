package com.epam.parking.service;

import com.epam.parking.dto.update.PermitUpdateUpdateDto;
import com.epam.parking.model.PermitUpdateHistory;
import com.epam.parking.repository.PermitTypeRepository;
import com.epam.parking.repository.PermitUpdateRepository;
import com.epam.parking.util.GenericModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermitUpdateService {

    private final PermitUpdateRepository permitUpdateRepository;
    private final PermitTypeRepository permitTypeRepository;

    public PermitUpdateHistory savePermitUpdate(PermitUpdateHistory permitUpdate) {
        return permitUpdateRepository.save(permitUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PermitUpdateHistory updateById(PermitUpdateUpdateDto permitUpdateUpdateDto) {
        return permitUpdateRepository.save(GenericModelMapper.convertToClass(permitUpdateUpdateDto, PermitUpdateHistory.class));
    }

    public Optional<PermitUpdateHistory> findByDriverId(long id) {
        return permitUpdateRepository.findByDriverId(id);
    }
}
