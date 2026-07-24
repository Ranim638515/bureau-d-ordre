package com.gpro.bureau_ordre_backend;

import com.gpro.bureau_ordre_backend.model.AppUser;
import com.gpro.bureau_ordre_backend.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BureauOrdreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BureauOrdreBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner initUser(AppUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("ranim").isEmpty()) {
                AppUser u = new AppUser();
                u.setUsername("ranim");
                u.setPassword(encoder.encode("password123"));
                repo.save(u);
                System.out.println("Utilisateur de test créé : ranim / password123");
            }
        };
    }
}