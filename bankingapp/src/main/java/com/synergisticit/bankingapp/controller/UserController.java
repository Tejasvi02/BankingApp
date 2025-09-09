package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Role;
import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.repository.RoleRepository;
import com.synergisticit.bankingapp.repository.UserRepository;
import com.synergisticit.bankingapp.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    // ===== Helpers =====
    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    private Role getUserRoleOrThrow() {
        return roleRepo.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("Missing USER role in DB. Seed it first."));
    }

    private void populateCommon(Model model, Authentication auth) {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("allRoles", roleRepo.findAll());
        model.addAttribute("isAdmin", isAdmin(auth));
    }

    // ===== List + form (admin list + form OR self profile) =====
    @GetMapping
    public String listAndForm(@RequestParam(value = "id", required = false) Integer id,
                              Model model,
                              Authentication auth) {

        User formUser;
        if (id != null && id > 0) {
            formUser = userRepo.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));
        } else {
            formUser = new User(); // userId = 0 for new users
        }

        model.addAttribute("user", formUser);
        populateCommon(model, auth);

        // First load: do not show validation messages
        model.addAttribute("submitted", false);

        return "user-form";
    }

    // ===== Edit shortcut =====
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id) {
        return "redirect:/users?id=" + id;
    }

    // ===== Delete (guarded in SecurityConfig; soft guard here too) =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Authentication auth) {
        if (!isAdmin(auth)) {
            return "redirect:/users?error=forbidden";
        }
        userRepo.deleteById(id);
        return "redirect:/users?deleted";
    }

    // ===== Save (Create/Update) with validation =====
    @PostMapping("/save")
    public String save(@ModelAttribute("user") User incoming,
                       BindingResult result,                         // MUST be right after @ModelAttribute
                       @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
                       Authentication auth,
                       HttpServletRequest request,
                       Model model) {

        boolean admin = isAdmin(auth);
        int id = incoming.getUserId();
        boolean isNew = (id == 0);

        // Normalize for validation + redisplay
        if (incoming.getUsername() != null) incoming.setUsername(incoming.getUsername().trim());
        if (incoming.getEmail() != null) incoming.setEmail(incoming.getEmail().trim());
        if (incoming.getPassword() != null) incoming.setPassword(incoming.getPassword().trim());

        // Keep the raw password the user actually submitted (blank means "keep existing" for edits)
        String rawPassword = incoming.getPassword() == null ? "" : incoming.getPassword().trim();
        boolean keepExistingPasswordOnEdit = (!isNew && !StringUtils.hasText(rawPassword));

        // If editing and password is blank, bypass validator's "password required" rule by
        // temporarily providing a dummy non-empty value. We'll NOT save it later.
        String originalPasswordForValidation = incoming.getPassword();
        if (keepExistingPasswordOnEdit) {
            incoming.setPassword("__KEEP_EXISTING__");
        }

        // 1) Base validation (required fields, simple rules inside your UserValidator)
        userValidator.validate(incoming, result);

        // restore what user actually sent
        incoming.setPassword(originalPasswordForValidation);

        // 2) Username uniqueness (nice message before DB exception)
        if (!result.hasFieldErrors("username") && StringUtils.hasText(incoming.getUsername())) {
            Optional<User> existingByName = userRepo.findByUsername(incoming.getUsername());
            if (isNew) {
                if (existingByName.isPresent()) {
                    result.rejectValue("username", "username.exists", "***Username already exists.");
                }
            } else {
                User dbUser = userRepo.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));
                if (!dbUser.getUsername().equals(incoming.getUsername()) && existingByName.isPresent()) {
                    result.rejectValue("username", "username.exists", "***Username already exists.");
                }
            }
        }

        // 3) New user must provide password
        if (isNew && !StringUtils.hasText(rawPassword)) {
            result.rejectValue("password", "password.required", "***Password is required for new users.");
        }

        // If any validation failed -> stay on page, show errors
        if (result.hasErrors()) {
            populateCommon(model, auth);
            model.addAttribute("submitted", true); // show errors now
            return "user-form";
        }

        // 4) Load target (new or existing), set fields
        User target = isNew ? new User() : userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));

        target.setUsername(incoming.getUsername());
        target.setEmail(incoming.getEmail());

        // Password:
        if (StringUtils.hasText(rawPassword)) {
            // New password supplied -> replace with encoded
            target.setPassword(passwordEncoder.encode(rawPassword));
        } else if (isNew) {
            // should not happen due to earlier checks; double-guard
            result.rejectValue("password", "password.required", "***Password is required for new users.");
            populateCommon(model, auth);
            model.addAttribute("submitted", true);
            return "user-form";
        } // else (editing + blank) -> keep existing password on target

        // Roles:
        List<Role> newRoles;
        if (admin) {
            if (roleIds != null && !roleIds.isEmpty()) {
                newRoles = roleRepo.findAllById(roleIds);
            } else {
                newRoles = Collections.singletonList(getUserRoleOrThrow());
            }
        } else {
            // Non-admins can only assign USER to anyone they create (or to themselves on update)
            newRoles = Collections.singletonList(getUserRoleOrThrow());
        }
        target.setRoles(newRoles);

        // 5) Save and redirect
        userRepo.save(target);
        return "redirect:/users?saved";
    }
}
