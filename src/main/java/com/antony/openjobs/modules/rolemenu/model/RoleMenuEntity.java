package com.antony.openjobs.modules.rolemenu.model;

import com.antony.openjobs.common.entities.BaseEntity;
import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;
import com.antony.openjobs.modules.roles.model.RoleEntity;

import jakarta.persistence.Entity;
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
    name = "role_menu",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"role_id", "menu_item_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    MenuItemEntity menuItem;
}
