package com.newbusiness.one4all.security;

import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;
import com.newbusiness.one4all.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;

import java.util.List;

@Aspect
@Component
public class RoleCheckAspect {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Around("@annotation(roleCheck)")
    public Object enforceRoles(ProceedingJoinPoint joinPoint, RoleCheck roleCheck) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userToken = request.getHeader("Authorization");

        if (userToken == null || !userToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(ResponseUtils.buildErrorResponse(
                    "UNAUTHORIZED", 401, "INVALID_USER_TOKEN"));
        }

        Jwt jwt = jwtDecoder.decode(userToken.replace("Bearer ", ""));
        List<String> tokenRoles = jwt.getClaimAsStringList("roles");

        if (tokenRoles == null || tokenRoles.stream().noneMatch(role -> List.of(roleCheck.value()).contains(role))) {
            return ResponseEntity.status(403).body(ResponseUtils.buildErrorResponse(
                    "FORBIDDEN", 403, "UNAUTHORIZED_ACCESS"));
        }

        return joinPoint.proceed();
    }
}
