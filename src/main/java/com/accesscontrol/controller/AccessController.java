package com.accesscontrol.controller;

import com.accesscontrol.entity.User;
import com.accesscontrol.service.AccessService;
import com.accesscontrol.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessController {
    private final AccessService accessService;
    private final UserService userService;

    public AccessController(AccessService accessService, UserService userService) {
        this.accessService = accessService;
        this.userService = userService;
    }

    @GetMapping("/accesses")
    public String showAccesses(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("accesses", accessService.getUserAccesses(user));
        return "accesses";
    }
}