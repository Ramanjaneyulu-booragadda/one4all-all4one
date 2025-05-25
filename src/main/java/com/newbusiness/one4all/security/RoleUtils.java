package com.newbusiness.one4all.security;

import java.util.Set;

import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleUtils {

    public static boolean hasRole(Member user, String roleName) {
        boolean result = user.getRoles().stream()
            .map(Role::getRoleName)
            .anyMatch(roleName::equals);
        log.info("Checking if user {} has role {}: {}", user.getOfaMemberId(), roleName, result);
        return result;
    }

    public static boolean hasAnyRole(Member user, String... roleNames) {
        Set<String> allowed = Set.of(roleNames);
        boolean result = user.getRoles().stream()
            .map(Role::getRoleName)
            .anyMatch(allowed::contains);
        log.info("Checking if user {} has any of roles {}: {}", user.getOfaMemberId(), allowed, result);
        return result;
    }
}
