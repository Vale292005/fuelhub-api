package com.example.fuel_community_api.infrastructure.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.fuel_community_api.domain.GasStation;
import com.example.fuel_community_api.infrastructure.repository.GasStationRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping("/gas-stations")
public class GasStationController {
    
    @Autowired 
    private GasStationRepository gasStationRepository;

    @GetMapping
    public List<GasStation> getAllGasStations() {
        return gasStationRepository.findAll();
    }

    @PostMapping("/report")
    public ResponseEntity<GasStation> reportGasStation(@RequestBody GasStation entity) {

        if (entity.getLat() == null || entity.getLng() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<GasStation> existingGasStation = gasStationRepository.findByLatAndLng(entity.getLat(), entity.getLng());
        if (existingGasStation.isPresent()) {
            GasStation gasStation = existingGasStation.get();
            gasStation.setPrice(entity.getPrice());
            gasStation.setHasPrice(true);
            gasStation.setLastUpdated(LocalDateTime.now());
            gasStationRepository.save(gasStation);
        } else {
            entity.setHasPrice(entity.getPrice() != null && !entity.getPrice().isEmpty());
            entity.setLastUpdated(LocalDateTime.now());
            gasStationRepository.save(entity);
        }
        return ResponseEntity.ok(entity);
    }
    
}
