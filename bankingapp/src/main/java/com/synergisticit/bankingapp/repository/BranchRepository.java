package com.synergisticit.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synergisticit.bankingapp.domain.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long>  {

}
