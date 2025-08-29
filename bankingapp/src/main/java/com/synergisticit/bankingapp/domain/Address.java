package com.synergisticit.bankingapp.domain;

import com.synergisticit.bankingapp.auditing.Auditable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Address { //have removed auditing here cos of error
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String zip;
}