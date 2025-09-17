package com.synergisticit.bankingapp.service;


import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.synergisticit.bankingapp.domain.Account;

public interface AccountService {
    Account save(Account account);
    void deleteById(Long accountId);

    List<Account> findAll();
    Optional<Account> findById(Long accountId);

    List<Account> findAllForUsername(String username);
}
