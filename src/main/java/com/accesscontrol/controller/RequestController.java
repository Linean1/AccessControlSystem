package com.accesscontrol.controller;

import com.accesscontrol.dao.*;
import com.accesscontrol.entity.*;
import com.accesscontrol.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RequestController {
    private final AccessRequestService accessRequestService;
    private final UserService userService;
    private final StatusRepository statusRepository;

    public RequestController(AccessRequestService accessRequestService, UserService userService, StatusRepository statusRepository) {
        this.accessRequestService = accessRequestService;
        this.userService = userService;
        this.statusRepository = statusRepository;
    }

    @GetMapping("/requests")
    public String showRequests(Authentication authentication, Model model, @RequestParam(required = false) String status) {
        User user = userService.findByUsername(authentication.getName());
        List<AccessRequest> requests = accessRequestService.getUserRequests(user, status);
        List<AccessRequest> ownerRequests = List.of();
        List<AccessRequest> adminRequests = List.of();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            adminRequests = accessRequestService.getRequestsForAdmin(user, "APPROVED_BY_OWNER");
        }
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
            ownerRequests = accessRequestService.getRequestsForOwner(user, "REQUESTED");
        }

        model.addAttribute("requests", requests);
        model.addAttribute("ownerRequests", ownerRequests);
        model.addAttribute("adminRequests", adminRequests);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("user", user);
        model.addAttribute("statuses", statusRepository.findAll());
        return "requests";
    }

    @GetMapping("/approve")
    public String approveRequest(@RequestParam Long requestId, Authentication authentication) {
        User owner = userService.findByUsername(authentication.getName());
        AccessRequest request = accessRequestService.findById(requestId);
        if (request != null && request.getRequestHistories().get(request.getRequestHistories().size() - 1).getStatus().getName().equals("REQUESTED")) {
            accessRequestService.updateRequestStatus(requestId, "APPROVED_BY_OWNER", owner.getId());
        }
        return "redirect:/requests";
    }

    @GetMapping("/reject")
    public String rejectRequest(@RequestParam Long requestId, Authentication authentication) {
        User owner = userService.findByUsername(authentication.getName());
        AccessRequest request = accessRequestService.findById(requestId);
        if (request != null && request.getRequestHistories().get(request.getRequestHistories().size() - 1).getStatus().getName().equals("REQUESTED")) {
            accessRequestService.updateRequestStatus(requestId, "REJECTED_BY_OWNER", owner.getId());
        }
        return "redirect:/requests";
    }

    @GetMapping("/admin_approve")
    public String adminApproveRequest(@RequestParam Long requestId, Authentication authentication) {
        User admin = userService.findByUsername(authentication.getName());
        AccessRequest request = accessRequestService.findById(requestId);
        if (request != null && request.getRequestHistories().get(request.getRequestHistories().size() - 1).getStatus().getName().equals("APPROVED_BY_OWNER")) {
            accessRequestService.updateRequestStatus(requestId, "GRANTED", admin.getId());
        }
        return "redirect:/requests";
    }

    @GetMapping("/admin_reject")
    public String adminRejectRequest(@RequestParam Long requestId, Authentication authentication) {
        User admin = userService.findByUsername(authentication.getName());
        AccessRequest request = accessRequestService.findById(requestId);
        if (request != null && request.getRequestHistories().get(request.getRequestHistories().size() - 1).getStatus().getName().equals("APPROVED_BY_OWNER")) {
            accessRequestService.updateRequestStatus(requestId, "REJECTED_BY_ADMIN", admin.getId());
        }
        return "redirect:/requests";
    }
}