package com.antony.openjobs.modules.posts.usecase.feed;

import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import java.time.LocalDateTime;
import java.util.UUID;

public record FeedResponse(UUID id, String content, String fileUrl, LocalDateTime createdAt,
        UserSummaryResponse author, long likesCount, long commentsCount, boolean liked) {}
