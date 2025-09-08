package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Address;
import com.synergisticit.bankingapp.domain.Branch;
import com.synergisticit.bankingapp.service.BranchService;
import com.synergisticit.bankingapp.validator.BranchValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @Autowired
    private BranchValidator branchValidator;

    // ========= List page with form (top) + table (bottom) =========
    @GetMapping
    public String listBranches(@RequestParam(defaultValue = "0") int page,
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
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

        // empty form model with embedded Address initialized
        Branch formBranch = new Branch();
        formBranch.setBranchAddress(new Address());
        model.addAttribute("branch", formBranch);

        // important: first load -> no errors shown
        model.addAttribute("submitted", false);

        return "branch-list";
    }

    // ========= Save (Add/Update) – stays on same page when errors =========
    @PostMapping
    public String saveBranch(@ModelAttribute("branch") Branch branch,
                             BindingResult result,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(defaultValue = "branchId") String sortField,
                             @RequestParam(defaultValue = "asc") String sortDir,
                             Model model) {

        // ensure embedded Address exists for nested binding/validation
        if (branch.getBranchAddress() == null) {
            branch.setBranchAddress(new Address());
        }

        // validate
        branchValidator.validate(branch, result);

        if (result.hasErrors()) {
            // reload list + pagination state
            Page<Branch> branchPage = branchService.getBranches(page, size, sortField, sortDir);

            model.addAttribute("branchPage", branchPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("size", size);
            model.addAttribute("totalPages", branchPage.getTotalPages());
            model.addAttribute("totalItems", branchPage.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

            // show errors only after submit
            model.addAttribute("submitted", true);

            // keep the user's entered values in the same form
            return "branch-list";
        }

        // no errors -> save and return to the list (preserve paging/sort via redirect params)
        branchService.saveBranch(branch);
        return "redirect:/branches?page=" + page +
               "&size=" + size +
               "&sortField=" + sortField +
               "&sortDir=" + sortDir;
    }

    // ========= Edit (pre-fill the same form on the list page) =========
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
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
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");

        Branch toEdit = branchService.getBranchById(id);
        if (toEdit.getBranchAddress() == null) {
            toEdit.setBranchAddress(new Address());
        }
        model.addAttribute("branch", toEdit);

        // editing view load -> do not show errors yet
        model.addAttribute("submitted", false);

        return "branch-list";
    }

    // ========= Delete =========
    @GetMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Long id,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "branchId") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDir) {

        branchService.deleteBranch(id);
        return "redirect:/branches?page=" + page +
               "&size=" + size +
               "&sortField=" + sortField +
               "&sortDir=" + sortDir;
    }
}
