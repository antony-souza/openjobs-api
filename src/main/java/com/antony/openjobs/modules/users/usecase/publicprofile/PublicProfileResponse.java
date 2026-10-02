package com.antony.openjobs.modules.users.usecase.publicprofile;

import com.antony.openjobs.modules.users.model.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public record PublicProfileResponse(
        UUID id, String name, String username, String avatarUrl, String role,
        String headline, String bio, String location, String portfolioUrl, String linkedinUrl,
        LocalDateTime joinedAt, long postsCount, String coverUrl
) {
    public static PublicProfileResponse from(UserEntity user, long postsCount) {
        return new PublicProfileResponse(user.getId(), user.getName(), user.getUsername(), user.getAvatarUrl(),
                user.getRole().getName(), user.getHeadline(), user.getBio(), user.getLocation(),
                user.getPortfolioUrl(), user.getLinkedinUrl(), user.getCreatedAt(), postsCount, user.getCoverUrl());
    }
}
