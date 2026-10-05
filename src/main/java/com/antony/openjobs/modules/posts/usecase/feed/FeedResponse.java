package com.antony.openjobs.modules.posts.usecase.feed;

import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedResponse(UUID id, String content, String fileUrl, OffsetDateTime createdAt,
        UserSummaryResponse author, long likesCount, long commentsCount, boolean liked) {}
