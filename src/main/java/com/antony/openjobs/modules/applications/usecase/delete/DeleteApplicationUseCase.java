package com.antony.openjobs.modules.applications.usecase.delete;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.applications.repository.ApplicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteApplicationUseCase {
    public final ApplicationRepository applicationRepository;

    public DeleteApplicationResponse execute(UUID applicationId, UUID candidateId) {
        var application = applicationRepository.findByIdAndCandidateIdAndDeletedAtIsNull(applicationId, candidateId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"));

        application.setDeletedAt(LocalDateTime.now());

        applicationRepository.save(application);

        return new DeleteApplicationResponse("Application deleted successfully");
    }
}
