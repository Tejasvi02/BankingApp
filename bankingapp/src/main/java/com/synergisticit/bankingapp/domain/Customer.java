package com.synergisticit.bankingapp.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.synergisticit.bankingapp.auditing.Auditable;
import com.synergisticit.bankingapp.enums.Gender;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Customer extends Auditable {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // will change strategy later
    private Long customerId;


    private String customerName;


    private Gender customerGender;


    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate customerDOB;


    private Address customerAddress;


    private String customerSSN;


    @OneToMany(mappedBy="accountCustomer")
    private List<Account> customerAccounts = new ArrayList<>();


    @OneToOne
    private User user;
}