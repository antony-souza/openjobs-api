package com.antony.openjobs.modules.posts.usecase.findall;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "content", "fileUrl", "createdAt" })
public interface FindAllPostsByUserIdProjection {
    UUID getId();

    String getContent();

    String getFileUrl();

    LocalDateTime getCreatedAt();
}
