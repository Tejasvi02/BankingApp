package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Account;
import com.synergisticit.bankingapp.domain.Customer;
import com.synergisticit.bankingapp.enums.AccountType;
import com.synergisticit.bankingapp.service.AccountService;
import com.synergisticit.bankingapp.service.BranchService;
import com.synergisticit.bankingapp.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final BranchService branchService;

    @GetMapping
    public String list(Model model, Authentication auth) {
        preparePage(model, auth, new Account());
        return "account";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("account") Account account,
                         BindingResult result,
                         Model model,
                         Authentication auth) {

        // Derive holder name from selected customer (no separate form field)
        if (account.getAccountCustomer() != null && account.getAccountCustomer().getCustomerId() != null) {
            Customer selected = customerService.findById(account.getAccountCustomer().getCustomerId())
                                               .orElse(null);
            if (selected != null) {
                account.setAccountCustomer(selected);
                account.setAccountHolder(selected.getCustomerName());
            } else {
                result.rejectValue("accountCustomer.customerId", "invalid.customer", "Select a valid customer");
            }
        } else {
            result.rejectValue("accountCustomer.customerId", "required.customer", "Select a customer");
        }

        if (account.getAccountBranch() == null || account.getAccountBranch().getBranchId() == null) {
            result.rejectValue("accountBranch.branchId", "required.branch", "Select a branch");
        } else {
            branchService.findById(account.getAccountBranch().getBranchId())
                         .ifPresent(account::setAccountBranch);
        }

        if (result.hasErrors()) {
            preparePage(model, auth, account);
            return "account";
        }

        accountService.save(account);
        model.addAttribute("success", "Account created.");
        preparePage(model, auth, new Account());
        return "account";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model, Authentication auth) {
        accountService.deleteById(id);
        model.addAttribute("success", "Account deleted.");
        preparePage(model, auth, new Account());
        return "account";
    }

    private void preparePage(Model model, Authentication auth, Account formBacking) {
        boolean isAdminOrMgr = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER"));

        // customers for the dropdown
        List<Customer> customers = isAdminOrMgr
                ? customerService.findAll()
                : (auth != null ? customerService.findAllByUserUsername(auth.getName()) : List.of());

        // accounts to display
        List<Account> accounts = isAdminOrMgr
                ? accountService.findAll()
                : (auth != null ? accountService.findAllForUsername(auth.getName()) : List.of());

        model.addAttribute("account", formBacking);
        model.addAttribute("accounts", accounts);
        model.addAttribute("customers", customers);
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("accountTypes", AccountType.values());
    }
    
    
}
