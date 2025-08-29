// DataSeeder.java (edited)
package com.synergisticit.bankingapp.config;

import com.synergisticit.bankingapp.domain.Role;
import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.repository.RoleRepository;
import com.synergisticit.bankingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Role adminRole = roleRepo.findByRoleName("ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setRoleName("ADMIN");
            return roleRepo.save(r);
        });

        Role userRole = roleRepo.findByRoleName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setRoleName("USER");
            return roleRepo.save(r);
        });

        // Upsert Admin
        User admin = userRepo.findByUsername("Admin").orElseGet(User::new);
        admin.setUsername("Admin");
        admin.setEmail("admin@example.com");
        // Always reset to a known dev password (only for dev!)
        admin.setPassword(passwordEncoder.encode("admin123"));
        List<Role> adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        // adminRoles.add(userRole); // if you want both
        admin.setRoles(adminRoles);
        userRepo.save(admin);

        // Upsert regular User
        User user = userRepo.findByUsername("User").orElseGet(User::new);
        user.setUsername("User");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("user123"));
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);
        userRepo.save(user);
    }
}
