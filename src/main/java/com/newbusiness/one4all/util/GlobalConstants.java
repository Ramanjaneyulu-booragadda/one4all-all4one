package com.newbusiness.one4all.util;

import java.util.Map;

public class GlobalConstants {
public static final String USER_REGISTRATION_SUCCESS="User created successfully.";
public static final String USER_LOGIN_SUCCESS="Login successful.";
public static final String USER_LOGIN_FAILED="Invalid member ID or password.";
public static final String PAYMENT_CREATION_SUCCESS="Payment successfully completed.";
public static final String PAYMENT_RETRIEVAL_SUCCESS="Payment record found.";
public static final String PAYMENT_NOT_FOUND="Payment record not found.";
public static final String PAYMENT_UPDATE_SUCCESS="Payment details successfully updated.";
public static final String PAYMENT_DELETION_SUCCESS="Payment record successfully deleted.";
public static final String PAYMENT_DISTRIBUTED_SUCCESS="Payment distribution successfully completed to upliners.";
public static final String REFFERAL_CREATION_ERROR="The referrer already has two direct downliners";
public static final String DUPLICATE_PAYMENT_RECORD_FOUND="Record already exists with the provided ofaConsumerNo:";
public static final String REFFERAR_CREATION_SUCCESS="successfully added refferar .";
public static final String DUPLICATE_REFFERAR_RECORD_FOUND="Record already exists with the provided member ID:";
public static final String CLIENT_ID="one4all";
public static final String ROLE_ADMIN_RW = "ONE4ALL_ADMIN_RW";
public static final String ROLE_ADMIN_W = "ONE4ALL_ADMIN_W";
public static final String ROLE_ADMIN_RO = "ONE4ALL_ADMIN_RO";
public static final String ROLE_USER_RO = "ONE4ALL_USER_RO";
public static final String ROLE_USER_RW = "ONE4ALL_USER_RW";
public static final String ROLE_USER_W = "ONE4ALL_USER_W";
public static final Map<String, String> ERROR_MESSAGES = Map.of(
        "INVALID_CLIENT_TOKEN", "Invalid or missing client token.",
        "INVALID_USER_TOKEN", "Invalid or missing user token.",
        "USER_LOGIN_FAILED", "Invalid credentials.",
        "TOKEN_EXPIRED", "The provided token has expired.",
        "UNAUTHORIZED_ACCESS", "You do not have permission to access this resource."
    );

    
}
