package com.accesscontrol.service;

import com.accesscontrol.dao.*;
import com.accesscontrol.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessRequestServiceTest {

    @Mock
    private AccessRequestRepository accessRequestRepository;

    @Mock
    private RequestHistoryRepository requestHistoryRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private AccessRequestService accessRequestService;

    private User user;
    private Service service;
    private Role role;
    private Status requestedStatus;

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

        requestedStatus = new Status();
        requestedStatus.setId(1L);
        requestedStatus.setName("REQUESTED");
    }

    @Test
    void testCreateRequest() {
        System.out.println("Тест testCreateRequest: Проверяется создание новой заявки и добавление истории...");
        when(accessRequestRepository.findByUser(user)).thenReturn(new ArrayList<>());
        when(accessRequestRepository.save(any(AccessRequest.class))).thenAnswer(invocation -> {
            AccessRequest request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });
        when(statusRepository.findByName("REQUESTED")).thenReturn(requestedStatus);
        when(requestHistoryRepository.save(any(RequestHistory.class))).thenAnswer(invocation -> {
            RequestHistory history = invocation.getArgument(0);
            history.setId(1L);
            if (history.getRequest() != null) {
                if (history.getRequest().getRequestHistories() == null) {
                    history.getRequest().setRequestHistories(new ArrayList<>());
                }
                history.getRequest().getRequestHistories().add(history);
            }
            return history;
        });

        AccessRequest expectedRequest = new AccessRequest();
        expectedRequest.setId(1L);
        AccessRequest actualRequest = accessRequestService.createRequest(user, service, role);

        System.out.println("Ожидаемый ID заявки: " + expectedRequest.getId());
        System.out.println("Фактический ID заявки: " + actualRequest.getId());
        System.out.println("Ожидаемое количество историй: 1");
        System.out.println("Фактическое количество историй: " + actualRequest.getRequestHistories().size());
        System.out.println("Ожидаемый статус: REQUESTED");
        System.out.println("Фактический статус: " + actualRequest.getRequestHistories().get(0).getStatus().getName());

        assertNotNull(actualRequest.getId());
        assertEquals(1, actualRequest.getRequestHistories().size());
        assertEquals("REQUESTED", actualRequest.getRequestHistories().get(0).getStatus().getName());
        verify(accessRequestRepository, times(1)).save(any(AccessRequest.class));
        verify(requestHistoryRepository, times(1)).save(any(RequestHistory.class));
        System.out.println("Результат: Тест успешно пройден. Заявка создана с ID и историей.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testCreateDuplicateRequest() {
        System.out.println("Тест testCreateDuplicateRequest: Проверяется обработка дубликата заявки...");
        AccessRequest existingRequest = new AccessRequest();
        existingRequest.setId(1L);
        existingRequest.setUser(user);
        existingRequest.setService(service);
        existingRequest.setRole(role);
        RequestHistory history = new RequestHistory();
        history.setId(1L);
        history.setStatus(requestedStatus);
        existingRequest.setRequestHistories(new ArrayList<>(List.of(history)));

        when(accessRequestRepository.findByUser(user)).thenReturn(List.of(existingRequest));

        Class<?> expectedException = RuntimeException.class;
        Class<?> actualException = null;
        try {
            accessRequestService.createRequest(user, service, role);
        } catch (Exception e) {
            actualException = e.getClass();
        }

        System.out.println("Ожидаемое исключение: " + expectedException.getSimpleName());
        System.out.println("Фактическое исключение: " + (actualException != null ? actualException.getSimpleName() : "Нет исключения"));

        assertThrows(RuntimeException.class, () -> accessRequestService.createRequest(user, service, role));
        verify(accessRequestRepository, never()).save(any(AccessRequest.class));
        System.out.println("Результат: Тест успешно пройден. Исключение выброшено при дубликате.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testUpdateRequestStatus() {
        System.out.println("Тест testUpdateRequestStatus: Проверяется обновление статуса заявки...");
        AccessRequest request = new AccessRequest();
        request.setId(1L);
        request.setUser(user);
        request.setService(service);
        request.setRole(role);

        Status approvedStatus = new Status();
        approvedStatus.setId(2L);
        approvedStatus.setName("APPROVED_BY_OWNER");

        when(accessRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(statusRepository.findByName("APPROVED_BY_OWNER")).thenReturn(approvedStatus);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accessRequestRepository.save(any(AccessRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accessRequestService.updateRequestStatus(1L, "APPROVED_BY_OWNER", 1L);

        int expectedHistorySize = 1;
        int actualHistorySize = request.getRequestHistories().size();
        String expectedStatus = "APPROVED_BY_OWNER";
        String actualStatus = request.getRequestHistories().get(0).getStatus().getName();

        System.out.println("Ожидаемое количество историй: " + expectedHistorySize);
        System.out.println("Фактическое количество историй: " + actualHistorySize);
        System.out.println("Ожидаемый статус: " + expectedStatus);
        System.out.println("Фактический статус: " + actualStatus);

        assertEquals(expectedHistorySize, actualHistorySize);
        assertEquals(expectedStatus, actualStatus);
        verify(accessRequestRepository, times(1)).save(request);
        System.out.println("Результат: Тест успешно пройден. Статус обновлён и история добавлена.");
        System.out.println("-------------------------------------------------------");
    }
}