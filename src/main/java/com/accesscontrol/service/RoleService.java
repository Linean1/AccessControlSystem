package com.accesscontrol.service;

import com.accesscontrol.entity.Role;
import com.accesscontrol.dao.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String name) {
        Role role = roleRepository.findByRoleName(name);
        if (role == null) {
            throw new RuntimeException("Role with name '" + name + "' not found");
        }
        return role;
    }

    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role with ID '" + id + "' not found"));
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}