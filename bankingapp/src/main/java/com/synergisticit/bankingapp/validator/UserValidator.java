package com.synergisticit.bankingapp.validator;

import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepo;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User u = (User) target;

        // basic required fields
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty", "Username is required.");
       // ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.empty", "Email is required.");

     // email rules:
        String email = u.getEmail();
        if (!StringUtils.hasText(email)) {
            errors.rejectValue("email", "email.required", "Email is required.");
        } else if (!email.contains("@")) {
            // Only show this when email is present but invalid
            errors.rejectValue("email", "email.invalid", "Email must contain @");
        }
        
        if (u.getUsername() != null && !u.getUsername().isBlank()) {
            userRepo.findByUsername(u.getUsername()).ifPresent(existing -> {
                // skip if editing the same user
                if (u.getUserId() == 0 || existing.getUserId() != u.getUserId()) {
                    errors.rejectValue("username", "username.duplicate", "Username already exists.");
                }
            });
        }

//        if (u.getEmail() != null && !u.getEmail().contains("@")) {
//            errors.rejectValue("email", "email.invalid", "Email must contain @");
//        }
    }
}
