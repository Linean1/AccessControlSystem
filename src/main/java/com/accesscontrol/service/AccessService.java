package com.accesscontrol.service;

import com.accesscontrol.entity.AccessRequest;
import com.accesscontrol.entity.User;
import com.accesscontrol.entity.RequestHistory; // Добавляем импорт
import com.accesscontrol.dao.AccessRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccessService {
    private final AccessRequestRepository accessRequestRepository;

    public AccessService(AccessRequestRepository accessRequestRepository) {
        this.accessRequestRepository = accessRequestRepository;
    }

    public List<AccessRequest> getUserAccesses(User user) {
        return accessRequestRepository.findByUser(user).stream()
                .filter(r -> {
                    // Проверяем последнюю историю заявки
                    List<RequestHistory> histories = r.getRequestHistories();
                    if (histories == null || histories.isEmpty()) return false;
                    RequestHistory latestHistory = histories.get(histories.size() - 1);
                    return latestHistory != null && "GRANTED".equals(latestHistory.getStatus().getName());
                })
                .collect(Collectors.toList());
    }
}