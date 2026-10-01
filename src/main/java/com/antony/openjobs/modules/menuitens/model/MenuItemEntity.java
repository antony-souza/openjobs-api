package com.antony.openjobs.modules.menuitens.model;

import com.antony.openjobs.common.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 50)
    String title;

    @Column(name = "icon_name", nullable = false, length = 50)
    String iconName;

    @Column(name = "path", nullable = false, length = 100)
    String path;
}
