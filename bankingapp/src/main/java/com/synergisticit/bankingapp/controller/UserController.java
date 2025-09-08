package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Role;
import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.repository.RoleRepository;
import com.synergisticit.bankingapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

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

    // ===== List + form =====
    @GetMapping
    public String listAndForm(@RequestParam(value = "id", required = false) Integer id,
                              Model model,
                              Authentication auth) {

        User formUser;
        if (id != null && id > 0) {
            formUser = userRepo.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));
        } else {
            formUser = new User(); // userId defaults to 0 (primitive)
        }

        model.addAttribute("user", formUser);
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("allRoles", roleRepo.findAll());
        model.addAttribute("isAdmin", isAdmin(auth));
        return "user-form";
    }

    // ===== Edit shortcut =====
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id) {
        return "redirect:/users?id=" + id;
    }

    // ===== Delete (ADMIN only in SecurityConfig; if not, also check here) =====
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Authentication auth) {
        // You can also protect via SecurityConfig. Keeping a simple guard here:
        if (!isAdmin(auth)) {
            return "redirect:/users?error=forbidden";
        }
        userRepo.deleteById(id);
        return "redirect:/users?deleted";
    }

    // ===== Save (Create/Update) =====
    @PostMapping("/save")
    public String save(@ModelAttribute("user") User incoming,
                       @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
                       Authentication auth,
                       HttpServletRequest request,
                       Model model) {

        boolean admin = isAdmin(auth);
        int id = incoming.getUserId(); // primitive int
        boolean isNew = (id == 0);

        // Normalize username
        String username = StringUtils.trimWhitespace(incoming.getUsername());
        if (!StringUtils.hasText(username)) {
            return withFormError("Username is required", incoming, admin, model);
        }

        // Uniqueness checks
        Optional<User> existingByName = userRepo.findByUsername(username);
        if (isNew) {
            if (existingByName.isPresent()) {
                return withFormError("Username already exists", incoming, admin, model);
            }
        } else {
            User dbUser = userRepo.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));
            // If changing username, ensure no other user uses it
            if (!dbUser.getUsername().equals(username) && existingByName.isPresent()) {
                return withFormError("Username already exists", incoming, admin, model);
            }
        }

        // Load target entity (new or existing)
        User target = isNew ? new User() : userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));

        // Set username + email
        target.setUsername(username);
        target.setEmail(StringUtils.trimWhitespace(incoming.getEmail()));

        // Password handling
        String rawPassword = StringUtils.trimWhitespace(incoming.getPassword());
        if (isNew) {
            if (!StringUtils.hasText(rawPassword)) {
                return withFormError("Password is required for new user", incoming, admin, model);
            }
            target.setPassword(passwordEncoder.encode(rawPassword));
        } else {
            // If provided, re-encode and replace. If blank, keep old.
            if (StringUtils.hasText(rawPassword)) {
                target.setPassword(passwordEncoder.encode(rawPassword));
            }
        }

        // Roles
        List<Role> newRoles;
        if (admin) {
            if (roleIds != null && !roleIds.isEmpty()) {
                newRoles = roleRepo.findAllById(roleIds);
            } else {
                // If admin didn't pick any, default USER
                newRoles = Collections.singletonList(getUserRoleOrThrow());
            }
        } else {
            // Non-admins can only assign USER
            newRoles = Collections.singletonList(getUserRoleOrThrow());
        }
        target.setRoles(newRoles);

        userRepo.save(target);
        return "redirect:/users?saved";
    }

    // ===== Utilities =====
    private String withFormError(String message, User formUser, boolean admin, Model model) {
        model.addAttribute("user", formUser);
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("allRoles", roleRepo.findAll());
        model.addAttribute("isAdmin", admin);
        model.addAttribute("error", message);
        return "user-form";
    }
}
