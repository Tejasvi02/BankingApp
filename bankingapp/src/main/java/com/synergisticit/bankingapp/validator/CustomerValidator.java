// com.synergisticit.bankingapp.validator.CustomerValidator
package com.synergisticit.bankingapp.validator;

import com.synergisticit.bankingapp.domain.Customer;
import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.repository.CustomerRepository;
import com.synergisticit.bankingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CustomerValidator implements Validator {

    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;

    @Override
    public boolean supports(Class<?> clazz) { return Customer.class.equals(clazz); }

    @Override
    public void validate(Object target, Errors errors) {
        Customer c = (Customer) target;

        // Requireds
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "customerName", "name.required", "Customer name is required.");
        if (c.getCustomerGender() == null) {
            errors.rejectValue("customerGender", "gender.required", "Gender is required.");
        }
        if (c.getCustomerDOB() == null) {
            errors.rejectValue("customerDOB", "dob.required", "Date of birth is required.");
        } else if (c.getCustomerDOB().isAfter(LocalDate.now())) {
            errors.rejectValue("customerDOB", "dob.future", "Date of birth cannot be in the future.");
        }

        // SSN basic check (###-##-#### or 9 digits)
        if (!StringUtils.hasText(c.getCustomerSSN())) {
            errors.rejectValue("customerSSN", "ssn.required", "SSN is required.");
        } else {
            String ssn = c.getCustomerSSN().trim();
            boolean ok = ssn.matches("\\d{3}-\\d{2}-\\d{4}") || ssn.matches("\\d{9}");
            if (!ok) errors.rejectValue("customerSSN", "ssn.format", "SSN must be 9 digits (with or without dashes).");
        }

        // Address basics
        if (c.getCustomerAddress() == null || !StringUtils.hasText(c.getCustomerAddress().getAddressLine1())) {
            errors.rejectValue("customerAddress.addressLine1", "addr.line1", "Address Line 1 is required.");
        }
        if (c.getCustomerAddress() == null || !StringUtils.hasText(c.getCustomerAddress().getCity())) {
            errors.rejectValue("customerAddress.city", "addr.city", "City is required.");
        }
        if (c.getCustomerAddress() == null || !StringUtils.hasText(c.getCustomerAddress().getState())) {
            errors.rejectValue("customerAddress.state", "addr.state", "State is required.");
        }
        if (c.getCustomerAddress() == null || !StringUtils.hasText(c.getCustomerAddress().getCountry())) {
            errors.rejectValue("customerAddress.country", "addr.country", "Country is required.");
        }
        if (c.getCustomerAddress() == null || !StringUtils.hasText(c.getCustomerAddress().getZip())) {
            errors.rejectValue("customerAddress.zip", "addr.zip", "Zip is required.");
        }

        // User pick required
        if (c.getUser() == null || c.getUser().getUserId() == 0) {
            errors.rejectValue("user.userId", "user.required", "Please select a user.");
            return;
        }

        // User must exist
        User picked = userRepo.findById(c.getUser().getUserId()).orElse(null);
        if (picked == null) {
            errors.rejectValue("user.userId", "user.invalid", "Selected user not found.");
            return;
        }

        // One user -> one customer
        var existing = customerRepo.findByUser_UserId(picked.getUserId());
        if (existing.isPresent()) {
            // Allow when editing self (same customer)
            if (c.getCustomerId() == null || !existing.get().getCustomerId().equals(c.getCustomerId())) {
                errors.rejectValue("user.userId", "user.taken", "This user already has a customer.");
            }
        }
    }
}
