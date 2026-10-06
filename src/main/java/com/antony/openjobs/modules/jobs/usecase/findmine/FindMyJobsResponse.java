package com.antony.openjobs.modules.jobs.usecase.findmine;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FindMyJobsResponse(UUID id, String title, String description, OffsetDateTime createdAt) {}
