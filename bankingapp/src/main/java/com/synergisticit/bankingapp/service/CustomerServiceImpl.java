package com.synergisticit.bankingapp.service;

import com.synergisticit.bankingapp.domain.Customer;
import com.synergisticit.bankingapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    @Override public Customer save(Customer c) { return repo.save(c); }

    @Override public void delete(Long id) { repo.deleteById(id); }

    @Override public Optional<Customer> findById(Long id) { return repo.findById(id); }

    @Override
    public Page<Customer> page(int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return repo.findAll(pageable);
    }

    @Override public long count() { return repo.count(); }


    @Override
    public List<Customer> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Customer> findAllByUserUsername(String username) {
        return repo.findAllByUserUsername(username);
    }
}

