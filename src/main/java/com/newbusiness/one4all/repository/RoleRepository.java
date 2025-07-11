package com.newbusiness.one4all.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbusiness.one4all.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
