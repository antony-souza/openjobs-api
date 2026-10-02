package com.antony.openjobs.modules.users.usecase;

import com.antony.openjobs.modules.users.model.UserEntity;

import java.util.UUID;

public record UserSummaryResponse(UUID id, String name, String username, String avatarUrl) {
    public static UserSummaryResponse from(UserEntity user) {
        return new UserSummaryResponse(user.getId(), user.getName(), user.getUsername(), user.getAvatarUrl());
    }
}
