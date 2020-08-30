package com.epam.parking.service;

import com.epam.parking.dto.update.SpotUpdateDto;
import com.epam.parking.exception.*;
import com.epam.parking.model.PermitUpdateHistory;
import com.epam.parking.model.Spot;
import com.epam.parking.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpotService {

    private final SpotRepository spotRepository;

    @Transactional(readOnly = true)
    public List<Spot> getSpotsByLocationId(long locationId) {
        return spotRepository.findAll()
                .stream()
                .filter(l -> l.getLocation().getId() == locationId)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Spot getSpotByTitleAndLocationId(long locationId, String title) {
        return getSpotsByLocationId(locationId)
                .stream()
                .filter(spot -> spot.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(Spot.class));
    }

    @Transactional(readOnly = true)
    public Spot getSpotById(long spotId) {
        return spotRepository.findById(spotId)
                .orElseThrow(() -> new NotFoundException(Spot.class));
    }

    public void deleteSpotById(long spotId) {
        if (getSpotById(spotId).getLinkedPermitUpdates().size() != 0) {
            throw new SpotIsNotEmptyException(Spot.class);
        }
        spotRepository.deleteById(spotId);
    }

    public Spot updateSpotById(long spotId, SpotUpdateDto spotUpdateDto) {
        Spot spot = getSpotById(spotId);
        long capacity = spotUpdateDto.getCapacity();
        String title = spotUpdateDto.getTitle();
        List<PermitUpdateHistory> permitUpdateList = spot.getLinkedPermitUpdates();

        if (capacity <= 0) {
            throw new SpotCapacityHasToBePositiveException(Spot.class);
        }

        if (permitUpdateList != null && capacity < permitUpdateList.size()) {
            throw new SpotCapacityCanNotBeLessThanOccupiedException(Spot.class);
        }

        spot.setCapacity(capacity);

        if (title.isEmpty()) {
            throw new SpotTitleNotEmptyException(Spot.class);
        }

        spot.setTitle(title);
        return spotRepository.save(spot);
    }
}