package com.antony.openjobs.modules.posts.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.utils.PostContentUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity extends BaseEntity {
    @Size(max = PostContentUtils.MAX_LENGTH, message = "A publicação deve ter no máximo 3000 caracteres")
    @Column(nullable = false, length = PostContentUtils.MAX_LENGTH)
    private String content;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
