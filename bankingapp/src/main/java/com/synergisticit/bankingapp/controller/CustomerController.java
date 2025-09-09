// com.synergisticit.bankingapp.controller.CustomerController
package com.synergisticit.bankingapp.controller;

import com.synergisticit.bankingapp.domain.Address;
import com.synergisticit.bankingapp.domain.Customer;
import com.synergisticit.bankingapp.domain.User;
import com.synergisticit.bankingapp.enums.Gender;
import com.synergisticit.bankingapp.repository.CustomerRepository;
import com.synergisticit.bankingapp.repository.UserRepository;
import com.synergisticit.bankingapp.service.CustomerService;
import com.synergisticit.bankingapp.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final CustomerValidator validator;

    private boolean isAdminOrManager(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            String role = a.getAuthority();
            if ("ROLE_ADMIN".equals(role) || "ROLE_MANAGER".equals(role)) return true;
        }
        return false;
    }

    /** Users without a customer (plus the one currently tied when editing). */
    private List<User> availableUsersFor(Customer currentOrNew) {
        // users already taken by customers
        Set<Integer> takenUserIds = customerRepo.findAll().stream()
                .map(c -> c.getUser() != null ? c.getUser().getUserId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (currentOrNew != null && currentOrNew.getUser() != null) {
            // keep the currently attached user selectable on edit
            takenUserIds.remove(currentOrNew.getUser().getUserId());
        }

        return userRepo.findAll().stream()
                .filter(u -> !takenUserIds.contains(u.getUserId()))
                .sorted(Comparator.comparing(User::getUsername))
                .collect(Collectors.toList());
    }

    private void addCommon(Model model, int page, int size, String sortField, String sortDir) {
        Page<Customer> p = customerService.page(page, size, sortField, sortDir);
        model.addAttribute("customerPage", p);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalItems", p.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("customerCount", customerService.count());
    }

    // List + form (top)
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model,
            Authentication auth
    ) {
        if (!isAdminOrManager(auth)) return "redirect:/home";

        addCommon(model, page, size, sortField, sortDir);

        Customer form = new Customer();
        form.setCustomerAddress(new Address());
        model.addAttribute("customer", form);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("availableUsers", availableUsersFor(form));
        model.addAttribute("submitted", false);

        return "customer-list";
    }

    // Save (create/update) stays on same page
    @PostMapping
    public String save(@ModelAttribute("customer") Customer customer,
                       BindingResult result,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "customerId") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model,
                       Authentication auth) {

        if (!isAdminOrManager(auth)) return "redirect:/home";

        if (customer.getCustomerAddress() == null) customer.setCustomerAddress(new Address());

        validator.validate(customer, result);
        if (result.hasErrors()) {
            addCommon(model, page, size, sortField, sortDir);
            model.addAttribute("customer", customer);
            model.addAttribute("genders", Gender.values());
            model.addAttribute("availableUsers", availableUsersFor(customer));
            model.addAttribute("submitted", true);
            return "customer-list";
        }

        customerService.save(customer);
        return "redirect:/customers?page=" + page + "&size=" + size + "&sortField=" + sortField + "&sortDir=" + sortDir;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "customerId") String sortField,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model,
                       Authentication auth) {
        if (!isAdminOrManager(auth)) return "redirect:/home";

        Customer c = customerService.findById(id).orElseThrow();
        addCommon(model, page, size, sortField, sortDir);
        model.addAttribute("customer", c);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("availableUsers", availableUsersFor(c));
        model.addAttribute("submitted", false);
        return "customer-list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        customerService.delete(id);
        return "redirect:/customers";
    }
}

