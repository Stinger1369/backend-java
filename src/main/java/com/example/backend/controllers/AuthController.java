package com.example.backend.controllers;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        logger.info("Tentative de connexion avec email : " + user.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            logger.info("Utilisateur trouvé dans MongoDB : " + dbUser.getEmail());
            logger.info("Mot de passe stocké en base : " + dbUser.getPassword());

            // Vérification correcte du mot de passe haché
            boolean passwordMatches = passwordEncoder.matches(user.getPassword(), dbUser.getPassword());
            logger.info("Résultat de la comparaison des mots de passe : " + passwordMatches);

            if (passwordMatches) {
                String token = jwtUtil.generateToken(user.getEmail());
                logger.info("JWT généré avec succès : " + token);
                return token;
            } else {
                logger.warning("Mot de passe incorrect !");
                return "Mot de passe incorrect"; // Erreur si le mot de passe ne correspond pas
            }
        }

        logger.warning("Utilisateur non trouvé !");
        return "Utilisateur non trouvé"; // Erreur si l'utilisateur n'existe pas
    }
}
