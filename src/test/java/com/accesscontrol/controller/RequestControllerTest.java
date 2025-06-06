package com.accesscontrol.controller;

import com.accesscontrol.dao.StatusRepository;
import com.accesscontrol.entity.AccessRequest;
import com.accesscontrol.entity.Status;
import com.accesscontrol.entity.User;
import com.accesscontrol.service.AccessRequestService;
import com.accesscontrol.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private AccessRequestService accessRequestService;

    @Mock
    private UserService userService;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private RequestController requestController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userService.findByUsername("testUser")).thenReturn(user);
        when(authentication.getName()).thenReturn("testUser");
    }

    @Test
    void testShowRequests_UserOnly() {
        System.out.println("Тест testShowRequests_UserOnly: Проверяется отображение заявок для обычного пользователя...");
        List<AccessRequest> userRequests = new ArrayList<>();
        userRequests.add(new AccessRequest());
        when(accessRequestService.getUserRequests(user, null)).thenReturn(userRequests);

        String expectedView = "requests";
        String actualView = requestController.showRequests(authentication, model, null);

        System.out.println("Ожидаемый вид: " + expectedView);
        System.out.println("Фактический вид: " + actualView);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("requests", userRequests);
        verify(model).addAttribute("ownerRequests", Collections.emptyList());
        verify(model).addAttribute("adminRequests", Collections.emptyList());
        verify(model).addAttribute("selectedStatus", null);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute(eq("statuses"), anyList());
        System.out.println("Результат: Тест успешно пройден. Только My Requests отображены для обычного пользователя.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testShowRequests_OwnerRole() {
        System.out.println("Тест testShowRequests_OwnerRole: Проверяется отображение заявок для роли OWNER...");
        List<AccessRequest> userRequests = new ArrayList<>();
        userRequests.add(new AccessRequest());
        List<AccessRequest> ownerRequests = new ArrayList<>();
        ownerRequests.add(new AccessRequest());
        when(accessRequestService.getUserRequests(user, null)).thenReturn(userRequests);
        when(accessRequestService.getRequestsForOwner(user, "REQUESTED")).thenReturn(ownerRequests);
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_OWNER")));

        String expectedView = "requests";
        String actualView = requestController.showRequests(authentication, model, null);

        System.out.println("Ожидаемый вид: " + expectedView);
        System.out.println("Фактический вид: " + actualView);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("requests", userRequests);
        verify(model).addAttribute("ownerRequests", ownerRequests);
        verify(model).addAttribute("adminRequests", Collections.emptyList());
        verify(model).addAttribute("selectedStatus", null);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute(eq("statuses"), anyList());
        System.out.println("Результат: Тест успешно пройден. My Requests и Requests for Owner отображены для OWNER.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testShowRequests_AdminRole() {
        System.out.println("Тест testShowRequests_AdminRole: Проверяется отображение заявок для роли ADMIN...");
        List<AccessRequest> userRequests = new ArrayList<>();
        userRequests.add(new AccessRequest());
        List<AccessRequest> adminRequests = new ArrayList<>();
        adminRequests.add(new AccessRequest());
        when(accessRequestService.getUserRequests(user, null)).thenReturn(userRequests);
        when(accessRequestService.getRequestsForAdmin(user, "APPROVED_BY_OWNER")).thenReturn(adminRequests);
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        String expectedView = "requests";
        String actualView = requestController.showRequests(authentication, model, null);

        System.out.println("Ожидаемый вид: " + expectedView);
        System.out.println("Фактический вид: " + actualView);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("requests", userRequests);
        verify(model).addAttribute("adminRequests", adminRequests);
        verify(model).addAttribute("ownerRequests", Collections.emptyList());
        verify(model).addAttribute("selectedStatus", null);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute(eq("statuses"), anyList());
        System.out.println("Результат: Тест успешно пройден. My Requests и Requests for Admin отображены для ADMIN.");
        System.out.println("-------------------------------------------------------");
    }
}