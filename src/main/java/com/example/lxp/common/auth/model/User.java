package com.example.lxp.common.auth.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.CommonErrorCode;

public class User {

    private Long id;
    private Role role;

    private User(Long id, Role role) {
        this.id = id;
        this.role = role;
    }

    public static User from(Long userIdHeader, String userRoleHeader) {
        if (userIdHeader == null || userIdHeader <= 0 || userRoleHeader == null) {
            throw BusinessException.builder(CommonErrorCode.INVALID_AUTH_CONTEXT).build();
        }
        return new User(userIdHeader, Role.from(userRoleHeader));
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }
    
}
