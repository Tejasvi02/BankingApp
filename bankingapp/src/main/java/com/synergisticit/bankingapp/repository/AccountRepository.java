package com.synergisticit.bankingapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.synergisticit.bankingapp.domain.Account;
import com.synergisticit.bankingapp.domain.Customer;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByAccountCustomer(Customer customer);
    List<Account> findByAccountCustomerIn(List<Customer> customers);
}