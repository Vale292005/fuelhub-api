package com.example.fuel_community_api.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fuel_community_api.domain.GasStation;

@Repository 
public interface GasStationRepository extends JpaRepository<GasStation, Long> {

    Optional<GasStation> findByLatAndLng(Double lat, Double lng);
    
} 