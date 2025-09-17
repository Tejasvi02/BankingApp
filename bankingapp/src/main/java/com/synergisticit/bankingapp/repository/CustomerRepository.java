package com.synergisticit.bankingapp.repository;


import com.synergisticit.bankingapp.domain.Customer;
import com.synergisticit.bankingapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByUser(User user);
    Optional<Customer> findByUser_UserId(int userId);
    List<Customer> findAllByUserUsername(String username);

}