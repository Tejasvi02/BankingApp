package com.synergisticit.bankingapp.domain;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.synergisticit.bankingapp.auditing.Auditable;
import com.synergisticit.bankingapp.enums.AccountType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Account extends Auditable{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // will change strategy later
    private Long accountId;


    @Enumerated(EnumType.STRING)
    private AccountType accountType;


    private String accountHolder;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate accountDateOpened;


    private double accountBalance;


    @ManyToOne
    private Branch accountBranch;


    @ManyToOne
    @JoinColumn(name="customerId")
    private Customer accountCustomer;


}
