package com.antony.openjobs.modules.roles.repository;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    boolean existsByNameAndCode(String name, String code);
}
