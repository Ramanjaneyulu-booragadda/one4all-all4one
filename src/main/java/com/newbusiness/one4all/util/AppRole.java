package com.newbusiness.one4all.util;

public enum AppRole {
    ONE4ALL_ADMIN_RW,
    ONE4ALL_ADMIN_W,
    ONE4ALL_ADMIN_RO,
    ONE4ALL_USER_RW,
    ONE4ALL_USER_W,
    ONE4ALL_USER_RO;

	public boolean isAdmin() {
        return this.name().startsWith("ONE4ALL_ADMIN");
    }

    public boolean isReadOnly() {
        return this.name().endsWith("_RO");
    }

    public String getRoleName() {
        return this.name();
    }
}
