package com.synergisticit.bankingapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.synergisticit.bankingapp.domain.Branch;
import com.synergisticit.bankingapp.service.BranchService;

@Controller
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;


    // Show form to add new branch
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("branch", new Branch());
        return "branch-form";
    }

    // Save branch
    @PostMapping
    public String saveBranch(@ModelAttribute("branch") Branch branch) {
        branchService.saveBranch(branch);
        return "redirect:/branches";
    }

    // List with pagination + sorting
    @GetMapping
    public String listBranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "branchId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Page<Branch> branchPage = branchService.getBranches(page, size, sortField, sortDir);

        model.addAttribute("branchPage", branchPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", branchPage.getTotalPages());
        model.addAttribute("totalItems", branchPage.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        // for the form
        model.addAttribute("branch", new Branch());
        return "branch-list";
    }

//    // Save branch (then keep pagination params if you want)
//    @PostMapping
//    public String saveBranch(@ModelAttribute("branch") Branch branch) {
//        branchService.saveBranch(branch);
//        return "redirect:/branches";
//    }

    // Edit shows the same page, with the form pre-filled
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "branchId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Page<Branch> branchPage = branchService.getBranches(page, size, sortField, sortDir);

        model.addAttribute("branchPage", branchPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", branchPage.getTotalPages());
        model.addAttribute("totalItems", branchPage.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("branch", branchService.getBranchById(id));
        return "branch-list";
    }

    @GetMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return "redirect:/branches";
    }
}
