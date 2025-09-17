package com.synergisticit.bankingapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.synergisticit.bankingapp.domain.Branch;
import com.synergisticit.bankingapp.repository.BranchRepository;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public Branch saveBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public Branch updateBranch(Branch branch) {
        return branchRepository.save(branch); // save acts as update if id is set
    }

    @Override
    public void deleteBranch(Long id) {
        branchRepository.deleteById(id);
    }

    @Override
    public Branch getBranchById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    @Override
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
    
    @Override
    public Page<Branch> getBranches(int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
        return branchRepository.findAll(PageRequest.of(page, size, sort));
    }

//	@Override
//	public List<Branch> findAll() {
//		return branchRepository.findAll(); 
//	}

	@Override
	public Optional<Branch> findById(Long id) {
		 return branchRepository.findById(id);
	}
    
}
