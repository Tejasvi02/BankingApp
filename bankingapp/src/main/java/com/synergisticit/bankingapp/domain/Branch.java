package com.synergisticit.bankingapp.domain;

import java.util.ArrayList;
import java.util.List;
import com.synergisticit.bankingapp.auditing.Auditable;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Branch extends Auditable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long branchId;
    private String branchName;
    
    @Embedded
    private Address branchAddress;

}
