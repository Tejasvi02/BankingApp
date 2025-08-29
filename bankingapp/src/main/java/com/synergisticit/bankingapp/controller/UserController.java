package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Role;
import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.repository.RoleRepository;
import com.synergisticit.bankingapp.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping
    public String userPage(Authentication auth, Model model) {
        boolean isAdmin = isAdmin(auth);
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            model.addAttribute("users", userRepo.findAll());
            model.addAttribute("allRoles", roleRepo.findAll());
            model.addAttribute("user", new User());
        } else {
            User me = userRepo.findByUsername(auth.getName()).orElseThrow();
            model.addAttribute("user", me);
        }
        return "user-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Integer id, Model model) {
        User u = userRepo.findById(id).orElseThrow();
        model.addAttribute("isAdmin", true);
        model.addAttribute("user", u);
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("allRoles", roleRepo.findAll());
        return "user-form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User formUser,
                           BindingResult binding,
                           Authentication auth,
                           @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
                           Model model) {

        boolean isAdmin = isAdmin(auth);

        if (!isAdmin && formUser.getUserId() != 0) {
            User me = userRepo.findByUsername(auth.getName()).orElseThrow();
            if (!Objects.equals(me.getUserId(), formUser.getUserId())) {
                binding.reject("forbidden", "You can only edit your own profile.");
            }
        }

        if (binding.hasErrors()) {
            model.addAttribute("isAdmin", isAdmin);
            if (isAdmin) {
                model.addAttribute("users", userRepo.findAll());
                model.addAttribute("allRoles", roleRepo.findAll());
            }
            return "user-form";
        }

        User toSave;
        if (formUser.getUserId() == 0) {
            toSave = new User();
        } else {
            toSave = userRepo.findById(formUser.getUserId()).orElseThrow();
        }

        toSave.setUsername(formUser.getUsername());
        toSave.setEmail(formUser.getEmail());

        if (formUser.getUserId() == 0 || (formUser.getPassword() != null && !formUser.getPassword().isBlank())) {
            toSave.setPassword(passwordEncoder.encode(formUser.getPassword()));
        }

        if (isAdmin) {
            List<Role> roles = (roleIds == null || roleIds.isEmpty())
                    ? ensureDefaultUserRole()
                    : roleIds.stream()
                             .map(roleRepo::findById)
                             .filter(Optional::isPresent)
                             .map(Optional::get)
                             .collect(Collectors.toList());
            toSave.setRoles(roles);
        } else {
            toSave.setRoles(ensureDefaultUserRole());
        }

        userRepo.save(toSave);
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        userRepo.deleteById(id);
        return "redirect:/users";
    }

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private List<Role> ensureDefaultUserRole() {
        Role userRole = roleRepo.findByRoleName("USER")
                .orElseThrow(() -> new IllegalStateException("Missing USER role"));
        return new ArrayList<>(List.of(userRole));
    }
}
