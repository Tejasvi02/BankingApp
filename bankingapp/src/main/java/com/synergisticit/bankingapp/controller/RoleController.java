package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Role;
import com.synergisticit.bankingapp.repository.RoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepo;

    @GetMapping
    public String roleForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roles", roleRepo.findAll());
        return "role-form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("role", roleRepo.findById(id).orElseThrow());
        model.addAttribute("roles", roleRepo.findAll());
        return "role-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("role") Role role, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("roles", roleRepo.findAll());
            return "role-form";
        }
        roleRepo.save(role);
        return "redirect:/roles";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        roleRepo.deleteById(id);
        return "redirect:/roles";
    }
}
