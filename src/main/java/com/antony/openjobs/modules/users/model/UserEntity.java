package com.antony.openjobs.modules.users.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String code;

    @Column(nullable = false)
    @Size(min = 6, max = 100)
    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RoleEntity role;
}
