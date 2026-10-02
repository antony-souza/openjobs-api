package com.antony.openjobs.modules.users.usecase;

import com.antony.openjobs.modules.users.model.UserEntity;
import java.util.UUID;

public record ProfileResponse(UUID id, String name, String email, String username, String avatarUrl, String role) {
    public static ProfileResponse from(UserEntity user) {
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getUsername(),
                user.getAvatarUrl(), user.getRole().getName());
    }
}
