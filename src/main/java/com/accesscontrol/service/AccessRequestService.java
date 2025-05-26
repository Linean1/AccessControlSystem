package com.accesscontrol.service;

import com.accesscontrol.entity.*;
import com.accesscontrol.dao.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccessRequestService {
    private final AccessRequestRepository accessRequestRepository;
    private final RequestHistoryRepository requestHistoryRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ServiceRepository serviceRepository;

    public AccessRequestService(AccessRequestRepository accessRequestRepository,
                                RequestHistoryRepository requestHistoryRepository,
                                StatusRepository statusRepository,
                                UserRepository userRepository,
                                RoleRepository roleRepository,
                                ServiceRepository serviceRepository) {
        this.accessRequestRepository = accessRequestRepository;
        this.requestHistoryRepository = requestHistoryRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.serviceRepository = serviceRepository;
    }

    public AccessRequest createRequest(User user, com.accesscontrol.entity.Service service, Role role) {
        List<AccessRequest> existingRequests = accessRequestRepository.findByUser(user);
        boolean exists = existingRequests.stream()
                .anyMatch(r -> r.getService().getId().equals(service.getId()) &&
                        r.getRole().getId().equals(role.getId()) &&
                        r.getRequestHistories().stream()
                                .anyMatch(h -> "REQUESTED".equals(h.getStatus().getName()) ||
                                        "APPROVED_BY_OWNER".equals(h.getStatus().getName())));
        if (exists) {
            throw new RuntimeException("Заявка на этот сервис с этой ролью уже существует и находится в обработке.");
        }

        AccessRequest request = new AccessRequest();
        request.setUser(user);
        request.setService(service);
        request.setRole(role);
        request.setRequestDate(LocalDateTime.now());
        request = accessRequestRepository.save(request);

        Status requestedStatus = statusRepository.findByName("REQUESTED");
        if (requestedStatus == null) throw new RuntimeException("Status 'REQUESTED' not found");
        RequestHistory history = new RequestHistory();
        history.setRequest(request);
        history.setStatus(requestedStatus);
        history.setChangeDate(LocalDateTime.now());
        history.setAuthor(user);
        requestHistoryRepository.save(history);

        return request;
    }

    public void updateRequestStatus(Long requestId, String statusName, Long authorId) {
        AccessRequest request = findById(requestId);
        if (request != null) {
            Status status = statusRepository.findByName(statusName);
            if (status == null) {
                throw new IllegalArgumentException("Status not found: " + statusName);
            }
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + authorId));
            RequestHistory history = new RequestHistory();
            history.setRequest(request);
            history.setStatus(status);
            history.setAuthor(author);
            history.setChangeDate(LocalDateTime.now());
            request.getRequestHistories().add(history);
            accessRequestRepository.save(request);
        }
    }

    public List<AccessRequest> getUserRequests(User user, String statusName) {
        List<AccessRequest> requests = accessRequestRepository.findByUser(user);
        if (statusName != null && !statusName.isEmpty()) {
            return requests.stream()
                    .filter(r -> r.getRequestHistories().stream()
                            .anyMatch(h -> h.getStatus().getName().equals(statusName)))
                    .collect(Collectors.toList());
        }
        return requests;
    }

    public List<AccessRequest> getRequestsForOwner(User owner, String statusName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !hasRole(auth, "OWNER")) {
            return List.of();
        }
        List<Long> ownedServiceIds = accessRequestRepository.findByUser(owner).stream()
                .filter(r -> r.getRequestHistories().stream()
                        .anyMatch(h -> "GRANTED".equals(h.getStatus().getName()) &&
                                r.getRole().getRoleName().equals("OWNER")))
                .map(r -> r.getService().getId())
                .distinct()
                .collect(Collectors.toList());

        return accessRequestRepository.findAll().stream()
                .filter(r -> ownedServiceIds.contains(r.getService().getId()))
                .filter(r -> r.getRequestHistories().stream()
                        .anyMatch(h -> "REQUESTED".equals(h.getStatus().getName())))
                .collect(Collectors.toList());
    }

    public List<AccessRequest> getRequestsForAdmin(User admin, String statusName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !hasRole(auth, "ADMIN")) {
            return List.of();
        }
        List<Long> adminServiceIds = accessRequestRepository.findByUser(admin).stream()
                .filter(r -> r.getRequestHistories().stream()
                        .anyMatch(h -> "GRANTED".equals(h.getStatus().getName()) &&
                                r.getRole().getRoleName().equals("ADMIN")))
                .map(r -> r.getService().getId())
                .distinct()
                .collect(Collectors.toList());

        return accessRequestRepository.findAll().stream()
                .filter(r -> adminServiceIds.contains(r.getService().getId()))
                .filter(r -> r.getRequestHistories().stream()
                        .anyMatch(h -> "APPROVED_BY_OWNER".equals(h.getStatus().getName())))
                .collect(Collectors.toList());
    }

    public boolean hasRole(Authentication auth, String roleName) {
        User user = userRepository.findByUsername(auth.getName());
        if (user != null) {
            return accessRequestRepository.findByUser(user).stream()
                    .anyMatch(r -> r.getRequestHistories().stream()
                            .anyMatch(h -> "GRANTED".equals(h.getStatus().getName()) &&
                                    r.getRole().getRoleName().equals(roleName)));
        }
        return false;
    }

    public AccessRequest findById(Long requestId) {
        return accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    public AccessRequest save(AccessRequest request) {
        return accessRequestRepository.save(request);
    }

    public List<Role> getUserRoles(User user) {
        return accessRequestRepository.findByUser(user).stream()
                .filter(r -> r.getRequestHistories().stream()
                        .anyMatch(h -> "GRANTED".equals(h.getStatus().getName())))
                .map(AccessRequest::getRole)
                .distinct()
                .collect(Collectors.toList());
    }
}