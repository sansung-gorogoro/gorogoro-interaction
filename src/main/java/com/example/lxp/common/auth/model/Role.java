package com.example.lxp.common.auth.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.CommonErrorCode;

public enum Role {
    ADMIN,
    INSTRUCTOR,
    STUDENT;

    public static Role from(String value) {
        if (value == null || value.isBlank()) {
            throw BusinessException.builder(CommonErrorCode.INVALID_AUTH_CONTEXT).build();
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw BusinessException.builder(CommonErrorCode.INVALID_AUTH_CONTEXT).build();
        }
    }

}
