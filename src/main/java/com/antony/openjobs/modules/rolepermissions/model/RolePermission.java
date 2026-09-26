package com.antony.openjobs.modules.rolepermissions.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.roles.model.RoleEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity
@Table(
    name = "role_permissions", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_role_permission",
            columnNames = { 
                "role_id", 
                "permission_id"
             }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permission;
}
