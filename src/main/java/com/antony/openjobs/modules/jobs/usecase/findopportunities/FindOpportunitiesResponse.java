package com.antony.openjobs.modules.jobs.usecase.findopportunities;

import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindOpportunitiesResponse(
        UUID id, String title, String description, LocalDateTime createdAt, UserSummaryResponse publishedBy
) {}
