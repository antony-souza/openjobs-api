package com.antony.openjobs.modules.users.usecase;

import com.antony.openjobs.modules.users.model.UserEntity;
import java.util.UUID;

public record ProfileResponse(
        UUID id, String name, String email, String username, String avatarUrl, String role,
        String headline, String bio, String location, String portfolioUrl, String linkedinUrl, String coverUrl
) {
    public ProfileResponse(UUID id, String name, String email, String username, String avatarUrl, String role) {
        this(id, name, email, username, avatarUrl, role, null, null, null, null, null, null);
    }
    public static ProfileResponse from(UserEntity user) {
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getUsername(),
                user.getAvatarUrl(), user.getRole().getName(),
                user.getHeadline(), user.getBio(), user.getLocation(), user.getPortfolioUrl(), user.getLinkedinUrl(), user.getCoverUrl());
    }
}
