package com.synergisticit.bankingapp.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.synergisticit.bankingapp.domain.Account;

@Component
public class AccountValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Account.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Account a = (Account) target;

        if (a.getAccountHolder() == null || a.getAccountHolder().isBlank()) {
            errors.rejectValue("accountHolder", "accountHolder.required", "Account holder is required");
        }
        if (a.getAccountType() == null) {
            errors.rejectValue("accountType", "accountType.required", "Account type is required");
        }
        if (a.getAccountCustomer() == null || a.getAccountCustomer().getCustomerId() == null) {
            errors.rejectValue("accountCustomer.customerId", "customer.required", "Customer is required");
        }
        if (a.getAccountBranch() == null || a.getAccountBranch().getBranchId() == null) {
            errors.rejectValue("accountBranch.branchId", "branch.required", "Branch is required");
        }
        if (a.getAccountBalance() < 0) {
            errors.rejectValue("accountBalance", "balance.invalid", "Balance cannot be negative");
        }
    }
}
