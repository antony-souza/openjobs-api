package com.antony.openjobs.modules.users.usecase.delete;

import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {
    private final UserRepository userRepository;

    @Transactional
    public DeleteUserResponse execute(UUID userId) {
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername("del-" + userId + "-" + user.getUsername());
        user.setEmail("del-" + userId + "-" + user.getEmail());
        user.setDeletedAt(LocalDateTime.now());

        return new DeleteUserResponse("User deleted successfully");
    }
}
