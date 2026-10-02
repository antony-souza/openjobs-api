package com.antony.openjobs.modules.users.services;

import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PublicProfileLookupService {
    private final IUserRepository userRepository;

    public UserEntity findActiveProfile(String username) {
        return userRepository.findByUsernameIgnoreCaseAndDeletedAtIsNull(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));
    }
}
