package com.antony.openjobs.modules.jobs.usecase.findall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"id", "title", "description", "publishedBy"})
public interface FindAllJobsProjection {
    UUID getId();

    String getTitle();

    String getDescription();

    @JsonProperty("publishedBy")
    String getPublishedByName();
}
