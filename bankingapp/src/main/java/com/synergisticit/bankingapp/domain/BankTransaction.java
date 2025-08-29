package com.synergisticit.bankingapp.domain;

import java.time.LocalDateTime;

import com.synergisticit.bankingapp.auditing.Auditable;
import com.synergisticit.bankingapp.enums.BankTransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class BankTransaction extends Auditable{


@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private Long bankTransactionId;


// for transfers and withdrawals (sender)
private Long bankTransactionFromAccount;


// for deposits and transfers (receiver)
private Long bankTransactionToAccount;


private BankTransactionType bankTransactionType;


private LocalDateTime bankTransactionDate;


private String comments;


}
