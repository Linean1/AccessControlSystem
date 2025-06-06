package com.accesscontrol.service;

import com.accesscontrol.dao.AccessRequestRepository;
import com.accesscontrol.entity.AccessRequest;
import com.accesscontrol.entity.RequestHistory;
import com.accesscontrol.entity.Status;
import com.accesscontrol.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {

    @Mock
    private AccessRequestRepository accessRequestRepository;

    @InjectMocks
    private AccessService accessService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
    }

    @Test
    void testGetUserAccesses_WithGrantedAccess() {
        System.out.println("Тест testGetUserAccesses_WithGrantedAccess: Проверяется получение доступа для пользователя с GRANTED статусом...");
        AccessRequest request = new AccessRequest();
        request.setId(1L);
        request.setUser(user);

        Status grantedStatus = new Status();
        grantedStatus.setId(1L);
        grantedStatus.setName("GRANTED");

        RequestHistory history = new RequestHistory();
        history.setId(1L);
        history.setStatus(grantedStatus);

        request.setRequestHistories(new ArrayList<>(List.of(history)));

        when(accessRequestRepository.findByUser(user)).thenReturn(List.of(request));

        List<AccessRequest> expectedAccesses = new ArrayList<>();
        expectedAccesses.add(request);
        List<AccessRequest> actualAccesses = accessService.getUserAccesses(user);

        System.out.println("Ожидаемое количество доступов: " + expectedAccesses.size());
        System.out.println("Фактическое количество доступов: " + actualAccesses.size());
        System.out.println("Ожидаемый доступ (ID): " + (expectedAccesses.size() > 0 ? expectedAccesses.get(0).getId() : "Нет"));
        System.out.println("Фактический доступ (ID): " + (actualAccesses.size() > 0 ? actualAccesses.get(0).getId() : "Нет"));

        assertEquals(1, actualAccesses.size());
        assertEquals(request, actualAccesses.get(0));
        System.out.println("Результат: Тест успешно пройден. Найдена 1 заявка с GRANTED статусом.");
        System.out.println("-------------------------------------------------------");
    }

    @Test
    void testGetUserAccesses_NoGrantedAccess() {
        System.out.println("Тест testGetUserAccesses_NoGrantedAccess: Проверяется получение доступа для пользователя без GRANTED статуса...");
        AccessRequest request = new AccessRequest();
        request.setId(1L);
        request.setUser(user);

        Status requestedStatus = new Status();
        requestedStatus.setId(1L);
        requestedStatus.setName("REQUESTED");

        RequestHistory history = new RequestHistory();
        history.setId(1L);
        history.setStatus(requestedStatus);

        request.setRequestHistories(new ArrayList<>(List.of(history)));

        when(accessRequestRepository.findByUser(user)).thenReturn(List.of(request));

        List<AccessRequest> expectedAccesses = new ArrayList<>();
        List<AccessRequest> actualAccesses = accessService.getUserAccesses(user);

        System.out.println("Ожидаемое количество доступов: " + expectedAccesses.size());
        System.out.println("Фактическое количество доступов: " + actualAccesses.size());

        assertEquals(0, actualAccesses.size());
        System.out.println("Результат: Тест успешно пройден. Заявок с GRANTED статусом не найдено.");
        System.out.println("-------------------------------------------------------");
    }
}