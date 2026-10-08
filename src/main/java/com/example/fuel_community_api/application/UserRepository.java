package com.example.fuel_community_api.application;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.fuel_community_api.domain.User;

/**
 * userRepository
 */
@Repository 
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


}
