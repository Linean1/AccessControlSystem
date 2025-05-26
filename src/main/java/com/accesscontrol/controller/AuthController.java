package com.accesscontrol.controller;

import com.accesscontrol.dao.*;
import com.accesscontrol.entity.*;
import com.accesscontrol.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;
    private final ServiceRepository serviceRepository;
    private final AccessRequestService accessRequestService;

    public AuthController(UserService userService, RoleService roleService, ServiceRepository serviceRepository,
                          AccessRequestService accessRequestService) {
        this.userService = userService;
        this.roleService = roleService;
        this.serviceRepository = serviceRepository;
        this.accessRequestService = accessRequestService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/request_access")
    public String requestAccessForm(Model model) {
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("services", serviceRepository.findAll());
        return "request_access";
    }

    @PostMapping("/request_access")
    public String submitRequest(@RequestParam Long serviceId, @RequestParam Long roleId, Authentication authentication, Model model) {
        try {
            User user = userService.findByUsername(authentication.getName());
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            Role role = roleService.findById(roleId);
            accessRequestService.createRequest(user, service, role);
            return "redirect:/requests";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при создании заявки: " + e.getMessage());
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("services", serviceRepository.findAll());
            return "request_access";
        }
    }

    @GetMapping("/request_edit")
    public String editRequest(@RequestParam Long requestId, Model model, Authentication authentication) {
        AccessRequest request = accessRequestService.findById(requestId);
        if (request == null || !request.getUser().getUsername().equals(authentication.getName())) {
            return "redirect:/requests?error=Unauthorized";
        }
        model.addAttribute("request", request);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("services", serviceRepository.findAll());
        return "request_edit";
    }

    @PostMapping("/update")
    public String updateRequest(@RequestParam Long requestId,
                                @RequestParam Long serviceId,
                                @RequestParam Long roleId,
                                Authentication authentication,
                                Model model) {
        AccessRequest request = accessRequestService.findById(requestId);
        if (request == null || !request.getUser().getUsername().equals(authentication.getName())) {
            return "redirect:/requests?error=Unauthorized";
        }
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        Role role = roleService.findById(roleId);
        request.setService(service);
        request.setRole(role);
        accessRequestService.save(request);
        return "redirect:/requests";
    }


    @GetMapping("/roles")
    public String showRoles(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("roles", accessRequestService.getUserRoles(user));
        return "roles";
    }
}