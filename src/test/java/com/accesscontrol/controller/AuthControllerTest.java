package com.accesscontrol.controller;

import com.accesscontrol.dao.ServiceRepository;
import com.accesscontrol.entity.Role;
import com.accesscontrol.entity.Service;
import com.accesscontrol.entity.User;
import com.accesscontrol.entity.AccessRequest;
import com.accesscontrol.service.AccessRequestService;
import com.accesscontrol.service.RoleService;
import com.accesscontrol.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private AccessRequestService accessRequestService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    private User user;
    private Service service;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        service = new Service();
        service.setId(1L);
        service.setServiceName("Test Service");

        role = new Role();
        role.setId(1L);
        role.setRoleName("USER");
    }

    @Test
    void testRequestAccessForm() {
        System.out.println("Тест testRequestAccessForm: Проверяется отображение формы подачи заявки...");
        when(roleService.findAll()).thenReturn(Collections.singletonList(role));
        when(serviceRepository.findAll()).thenReturn(Collections.singletonList(service));

        String expectedView = "request_access";
        String actualView = authController.requestAccessForm(model);

        // Вывод ожидаемого и фактического результата
        System.out.println("Ожидаемый вид: " + expectedView);
        System.out.println("Фактический вид: " + actualView);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("roles", Collections.singletonList(role));
        verify(model).addAttribute("services", Collections.singletonList(service));
        System.out.println("Результат: Тест успешно пройден. Форма отображена с ролями и сервисами.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testSubmitRequest_Success() {
        System.out.println("Тест testSubmitRequest_Success: Проверяется успешная отправка заявки...");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(roleService.findById(1L)).thenReturn(role);
        when(accessRequestService.createRequest(user, service, role)).thenReturn(new AccessRequest());

        String expectedView = "redirect:/requests";
        String actualView = authController.submitRequest(1L, 1L, authentication, model);

        System.out.println("Ожидаемый редирект: " + expectedView);
        System.out.println("Фактический редирект: " + actualView);

        assertEquals(expectedView, actualView);
        verify(accessRequestService).createRequest(user, service, role);
        System.out.println("Результат: Тест успешно пройден. Заявка отправлена, редирект на /requests.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testSubmitRequest_Failure() {
        System.out.println("Тест testSubmitRequest_Failure: Проверяется отправка заявки с ошибкой (дубликат)...");
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(roleService.findById(1L)).thenReturn(role);
        when(accessRequestService.createRequest(user, service, role)).thenThrow(new RuntimeException("Duplicate request"));
        when(roleService.findAll()).thenReturn(Collections.singletonList(role));
        when(serviceRepository.findAll()).thenReturn(Collections.singletonList(service));

        String expectedView = "request_access";
        String actualView = authController.submitRequest(1L, 1L, authentication, model);

        System.out.println("Ожидаемый вид при ошибке: " + expectedView);
        System.out.println("Фактический вид при ошибке: " + actualView);

        assertEquals(expectedView, actualView);
        verify(model).addAttribute("error", "Ошибка при создании заявки: Duplicate request");
        verify(model).addAttribute("roles", Collections.singletonList(role));
        verify(model).addAttribute("services", Collections.singletonList(service));
        System.out.println("Результат: Тест успешно пройден. Ошибка обработана, возвращена форма с сообщением.");
        System.out.println("-------------------------------------------------------");
    }
}