package com.synergisticit.bankingapp.validator;

import com.synergisticit.bankingapp.domain.Address;
import com.synergisticit.bankingapp.domain.Branch;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class BranchValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Branch.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Branch b = (Branch) target;

        // Branch name required
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "branchName", "branchName.empty", "***Branch name must not be empty.");

        // Address object must exist
        Address addr = b.getBranchAddress();
        if (addr == null) {
            errors.rejectValue("branchAddress", "branchAddress.null", "***Branch address is required.");
            return; // no nested checks possible
        }

        // Nested Address fields
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "branchAddress.addressLine1", "addressLine1.empty", "***Address Line 1 is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "branchAddress.city", "city.empty", "***City is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "branchAddress.state", "state.empty", "***State is required.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "branchAddress.country", "country.empty", "***Country is required.");

        // ZIP: optional pattern check if present
        String zip = addr.getZip();
        if (zip != null && !zip.isBlank()) {
            // US ZIP (12345 or 12345-6789). Tweak if you need international rules.
            if (!zip.matches("\\d{5}(-\\d{4})?")) {
                errors.rejectValue("branchAddress.zip", "zip.invalid", "***ZIP must be 12345 or 12345-6789.");
            }
        } else {
            errors.rejectValue("branchAddress.zip", "zip.empty", "***ZIP is required.");
        }
    }
}
