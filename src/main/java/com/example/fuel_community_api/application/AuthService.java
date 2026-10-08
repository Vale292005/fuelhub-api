package com.example.fuel_community_api.application;

import com.example.fuel_community_api.domain.User;
import com.example.fuel_community_api.infrastructure.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User register(String email, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        User user = new User();
        user.setEmail(email);
        // ¡Nunca guardes contraseñas en texto plano! Usamos BCrypt
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setTankCredits(0); // Inicia con 0 créditos en el tanque

        return userRepository.save(user);
    }

    public String login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verificamos si la contraseña coincide con la encriptada en la BD
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                // Si es correcta, generamos y devolvemos el token JWT
                return jwtUtil.generateToken(user.getEmail());
            }
        }
        throw new RuntimeException("Credenciales inválidas");
    }
}