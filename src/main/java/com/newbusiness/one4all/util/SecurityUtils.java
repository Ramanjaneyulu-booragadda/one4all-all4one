package com.newbusiness.one4all.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class SecurityUtils {

    public static boolean isValidClientToken(String clientToken, JwtDecoder jwtDecoder) {
        try {
            Jwt jwt = jwtDecoder.decode(clientToken.replace("Bearer ", ""));
            return jwt.getClaim("client_id") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidUserToken(String userToken, JwtDecoder jwtDecoder) {
        try {
            Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
            return jwt.getClaim("roles") != null;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if the user has any of the required roles
    public static boolean hasRequiredRole(String userToken, JwtDecoder jwtDecoder, Set<String> requiredRoles) {
        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        Set<String> userRoles = jwt.getClaimAsStringList("roles").stream().collect(Collectors.toSet());
        return userRoles.stream().anyMatch(requiredRoles::contains);
    }

    // Role hierarchy: for example, RW roles can perform both RO and W operations
    public static boolean hasPermissionForAction(String userToken, JwtDecoder jwtDecoder, String requiredPermission) {
        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        Set<String> userRoles = jwt.getClaimAsStringList("roles").stream().collect(Collectors.toSet());

        // Define role-based permission mappings
        Map<String, Set<String>> permissionMappings = Map.of(
            "READ", Set.of("ONE4ALL_ADMIN_RO", "ONE4ALL_ADMIN_RW", "ONE4ALL_USER_RO", "ONE4ALL_USER_RW"),
            "WRITE", Set.of("ONE4ALL_ADMIN_W", "ONE4ALL_ADMIN_RW", "ONE4ALL_USER_W", "ONE4ALL_USER_RW")
        );

        Set<String> rolesWithPermission = permissionMappings.getOrDefault(requiredPermission, Collections.emptySet());
        return userRoles.stream().anyMatch(rolesWithPermission::contains);
    }
}


