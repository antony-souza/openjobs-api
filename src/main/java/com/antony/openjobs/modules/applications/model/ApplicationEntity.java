package com.antony.openjobs.modules.applications.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.applications.enums.ApplicationStatus;
import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.users.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private UserEntity candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobEntity job;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;
}
