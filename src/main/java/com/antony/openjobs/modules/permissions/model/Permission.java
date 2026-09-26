package com.antony.openjobs.modules.permissions.model;

public enum Permission {
    USER_READ("USER_READ"),
    USER_UPDATE("USER_UPDATE"),
    USER_DELETE("USER_DELETE"),
    ROLE_READ("ROLE_READ"),
    ROLE_CREATE("ROLE_CREATE"),
    ROLE_UPDATE("ROLE_UPDATE"),
    JOB_READ("JOB_READ"),
    JOB_CREATE("JOB_CREATE"),
    JOB_UPDATE("JOB_UPDATE"),
    JOB_DELETE("JOB_DELETE"),
    APPLICATION_READ("APPLICATION_READ"),
    APPLICATION_CREATE("APPLICATION_CREATE"),
    APPLICATION_DELETE("APPLICATION_DELETE");

    private final String code;

    Permission(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
