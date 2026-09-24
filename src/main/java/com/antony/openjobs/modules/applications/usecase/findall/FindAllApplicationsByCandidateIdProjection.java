package com.antony.openjobs.modules.applications.usecase.findall;

import java.util.UUID;

import com.antony.openjobs.modules.applications.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "candidateId", "jobId", "status" })
public interface FindAllApplicationsByCandidateIdProjection {
    UUID getId();

    UUID getCandidateId();

    UUID getJobId();

    ApplicationStatus getStatus();
}
