package com.accesscontrol.service;

import com.accesscontrol.entity.User;
import com.accesscontrol.dao.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User with username '" + username + "' not found");
        }
        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}