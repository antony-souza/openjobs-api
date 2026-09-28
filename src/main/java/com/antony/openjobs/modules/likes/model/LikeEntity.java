package com.antony.openjobs.modules.likes.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.users.model.UserEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "likes", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "post_id" })
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;
}
