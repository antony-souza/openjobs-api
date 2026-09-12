package com.antony.openjobs.modules.users.repository;

import com.antony.openjobs.modules.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmailAndUsernameAndDeletedAtIsNull(String email, String username);
}
