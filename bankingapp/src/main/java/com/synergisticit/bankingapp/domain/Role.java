package com.synergisticit.bankingapp.domain;

import java.util.ArrayList;
import java.util.List;

import com.synergisticit.bankingapp.auditing.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Role extends Auditable{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int roleId;
    
    @Column(unique = true, nullable = false)
    private String roleName;
    
}