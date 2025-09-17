package com.synergisticit.bankingapp.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synergisticit.bankingapp.domain.Account;
import com.synergisticit.bankingapp.domain.Customer;
import com.synergisticit.bankingapp.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void deleteById(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAllForUsername(String username) {
        List<Customer> myCustomers = customerService.findAllByUserUsername(username);
        if (myCustomers == null || myCustomers.isEmpty()) return Collections.emptyList();
        return accountRepository.findByAccountCustomerIn(myCustomers);
    }
}
