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

    // ===== Page: admin = list + form; non-admin = add-user + my-profile (change password only)
    @GetMapping
    public String page(@RequestParam(value = "id", required = false) Integer id,
                       Model model,
                       Authentication auth,
                       @RequestParam(value="pwdChanged", required=false) String pwdChanged) {

        boolean admin = isAdmin(auth);
        model.addAttribute("isAdmin", admin);

        if (admin) {
            // ----- ADMIN VIEW -----
            User formUser;
            if (id != null && id > 0) {
                formUser = userRepo.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));
            } else {
                formUser = new User();
            }
            model.addAttribute("user", formUser);
            model.addAttribute("users", userRepo.findAll());
            model.addAttribute("allRoles", roleRepo.findAll());
            model.addAttribute("submitted", false);
            return "user-form";
        } else {
            // ----- NON-ADMIN VIEW -----
            // Card 1: Add User (modelAttribute="user")
            model.addAttribute("user", new User());
            model.addAttribute("submitted", false);

            // Card 2: My Profile (modelAttribute="me" for display; change password form posts separately)
            User me = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new NoSuchElementException("Current user not found"));
            model.addAttribute("me", me);
            model.addAttribute("pwdChanged", pwdChanged != null); // show success toast if present

            return "user-form"; // same JSP handles both modes
        }
    }

    // Shortcut for admin edit
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id) {
        return "redirect:/users?id=" + id;
    }

    // Delete (admin only via security)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Authentication auth) {
        if (!isAdmin(auth)) return "redirect:/users?error=forbidden";
        userRepo.deleteById(id);
        return "redirect:/users?deleted";
    }

    // ===== Create/Update user (admin: roles allowed; non-admin: role forced to USER)
    @PostMapping("/save")
    public String save(@ModelAttribute("user") User incoming,
                       BindingResult result,   // MUST follow @ModelAttribute
                       @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
                       Authentication auth,
                       HttpServletRequest request,
                       Model model) {

        boolean admin = isAdmin(auth);
        int id = incoming.getUserId();
        boolean isNew = (id == 0);

        // Normalize
        if (incoming.getUsername() != null) incoming.setUsername(incoming.getUsername().trim());
        if (incoming.getEmail() != null) incoming.setEmail(incoming.getEmail().trim());
        if (incoming.getPassword() != null) incoming.setPassword(incoming.getPassword().trim());

        // Keep raw password
        String rawPassword = incoming.getPassword() == null ? "" : incoming.getPassword().trim();
        boolean keepExistingPasswordOnEdit = (!isNew && !StringUtils.hasText(rawPassword));

        // Bypass validator's password-required rule for edits when blank
        String originalPwd = incoming.getPassword();
        if (keepExistingPasswordOnEdit) incoming.setPassword("__KEEP__");
        userValidator.validate(incoming, result);
        incoming.setPassword(originalPwd);

        // Username uniqueness (friendly message)
        if (!result.hasFieldErrors("username") && StringUtils.hasText(incoming.getUsername())) {
            Optional<User> existingByName = userRepo.findByUsername(incoming.getUsername());
            if (isNew) {
                if (existingByName.isPresent()) {
                    result.rejectValue("username", "username.exists", "Username already exists.");
                }
            } else {
                User dbUser = userRepo.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));
                if (!dbUser.getUsername().equals(incoming.getUsername()) && existingByName.isPresent()) {
                    result.rejectValue("username", "username.exists", "Username already exists.");
                }
            }
        }

        // New user must provide password
        if (isNew && !StringUtils.hasText(rawPassword)) {
            result.rejectValue("password", "password.required", "***Password is required for new users.");
        }

        if (result.hasErrors()) {
            if (admin) {
                model.addAttribute("users", userRepo.findAll());
                model.addAttribute("allRoles", roleRepo.findAll());
            } else {
                // non-admin view also needs "me"
                User me = userRepo.findByUsername(auth.getName())
                        .orElseThrow(() -> new NoSuchElementException("Current user not found"));
                model.addAttribute("me", me);
            }
            model.addAttribute("isAdmin", admin);
            model.addAttribute("submitted", true);
            return "user-form";
        }

        // Persist
        User target = isNew ? new User() : userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found id=" + id));

        target.setUsername(incoming.getUsername());
        target.setEmail(incoming.getEmail());

        if (StringUtils.hasText(rawPassword)) {
            target.setPassword(passwordEncoder.encode(rawPassword));
        } // else keep existing for edit

        // Roles
        List<Role> newRoles;
        if (admin) {
            if (roleIds != null && !roleIds.isEmpty()) {
                newRoles = roleRepo.findAllById(roleIds);
            } else {
                newRoles = Collections.singletonList(getUserRoleOrThrow());
            }
        } else {
            // Non-admin can only create USER
            newRoles = Collections.singletonList(getUserRoleOrThrow());
        }
        target.setRoles(newRoles);

        userRepo.save(target);
        return "redirect:/users?saved";
    }

    // ===== Change password for the CURRENT logged-in user only
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 Authentication auth,
                                 Model model) {
        if (auth == null || !StringUtils.hasText(auth.getName())) {
            return "redirect:/login";
        }
        newPassword = (newPassword == null) ? "" : newPassword.trim();
        if (newPassword.length() < 6) {
            // Re-render non-admin page with error (admin typically doesn’t use this path)
            User me = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new NoSuchElementException("Current user not found"));
            model.addAttribute("isAdmin", false);
            model.addAttribute("user", new User()); // blank add-user form
            model.addAttribute("me", me);
            model.addAttribute("submitted", false);
            model.addAttribute("pwdError", "Password must be at least 6 characters.");
            return "user-form";
        }

        User me = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new NoSuchElementException("Current user not found"));
        me.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(me);

        return "redirect:/users?pwdChanged=1";
    }
}
