package com.synergisticit.bankingapp.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.synergisticit.bankingapp.domain.Branch;

public interface BranchService {
    Branch saveBranch(Branch branch);
    Branch updateBranch(Branch branch);
    void deleteBranch(Long id);
    Branch getBranchById(Long id);
    List<Branch> getAllBranches();
    Page<Branch> getBranches(int page, int size, String sortField, String sortDir);
}
