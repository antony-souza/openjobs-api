package com.antony.openjobs.modules.jobs.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.users.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by", nullable = false)
    private UserEntity publishedBy;
}
