package com.newbusiness.one4all.security;

import java.util.Set;

import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.Role;

public class RoleUtils {

    public static boolean hasRole(Member user, String roleName) {
        return user.getRoles().stream()
            .map(Role::getRoleName)
            .anyMatch(roleName::equals);
    }

    public static boolean hasAnyRole(Member user, String... roleNames) {
        Set<String> allowed = Set.of(roleNames);
        return user.getRoles().stream()
            .map(Role::getRoleName)
            .anyMatch(allowed::contains);
    }
}
