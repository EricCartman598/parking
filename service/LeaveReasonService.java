package com.epam.parking.service;

import com.epam.parking.exception.NotFoundException;
import com.epam.parking.model.LeaveReason;
import com.epam.parking.repository.LeaveReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LeaveReasonService {

    private final LeaveReasonRepository leaveReasonRepository;

    @Transactional(readOnly = true)
    public LeaveReason findByTitle(String title) {
        return leaveReasonRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(LeaveReason.class));
    }
}
