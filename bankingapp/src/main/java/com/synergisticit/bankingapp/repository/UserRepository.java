package com.synergisticit.bankingapp.repository;

import com.synergisticit.bankingapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);                // for UserDetailsService
    Optional<User> findByUsernameIgnoreCase(String username);      // for uniqueness check
    boolean existsByUsernameIgnoreCase(String username);           // optional
}
