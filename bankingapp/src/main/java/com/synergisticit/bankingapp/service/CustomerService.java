package com.synergisticit.bankingapp.service;

import com.synergisticit.bankingapp.domain.Customer;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer save(Customer c);
    void delete(Long id);
    Optional<Customer> findById(Long id);
    Page<Customer> page(int page, int size, String sortField, String sortDir);
    long count();
    List<Customer> findAll();
    List<Customer> findAllByUserUsername(String username);

}
