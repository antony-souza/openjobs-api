package com.antony.openjobs.modules.users.repository;

import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.usecase.findall.FindAllUsersProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByUsernameAndDeletedAtIsNull(String username);

    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

    Page<FindAllUsersProjection> findAllByDeletedAtIsNull(Pageable pageable);
}
