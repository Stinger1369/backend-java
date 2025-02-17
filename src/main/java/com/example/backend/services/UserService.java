package com.example.backend.services;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        logger.info("Création d'un nouvel utilisateur avec email : " + user.getEmail());

        // Vérifie si l'utilisateur existe déjà
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warning("L'utilisateur existe déjà !");
            throw new RuntimeException("Cet utilisateur existe déjà !");
        }

        // Hacher le mot de passe avant de l'enregistrer
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Mot de passe haché pour l'utilisateur : " + user.getPassword());

        User savedUser = userRepository.save(user);
        logger.info("Utilisateur enregistré avec succès !");

        return savedUser;
    }

    public void deleteUser(String id) {
        logger.info("Suppression de l'utilisateur avec ID : " + id);
        userRepository.deleteById(id);
    }
}
