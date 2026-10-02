package com.antony.openjobs.modules.users.usecase.findprofile;

import com.antony.openjobs.modules.users.repository.IUserRepository;
import com.antony.openjobs.modules.users.usecase.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindProfileUseCase {
    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse execute(UUID userId) {
        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return ProfileResponse.from(user);
    }
}
