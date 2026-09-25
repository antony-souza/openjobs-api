package com.antony.openjobs.modules.applications.usecase.create;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.applications.enums.ApplicationStatus;
import com.antony.openjobs.modules.applications.model.ApplicationEntity;
import com.antony.openjobs.modules.applications.repository.ApplicationRepository;
import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateApplicationUseCase {
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public CreateApplicationResponse execute(UUID candidateId, CreateApplicationRequest request) {
        if (applicationRepository.existsByCandidateIdAndJobIdAndDeletedAtIsNull(candidateId,
                request.jobId())) {
            return new CreateApplicationResponse("Candidato já se candidatou para essa vaga");
        }

        ApplicationEntity applicationEntity = new ApplicationEntity();

        JobEntity jobEntity = jobRepository.findByIdAndDeletedAtIsNull(request.jobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        UserEntity userEntity = userRepository.findByIdAndDeletedAtIsNull(candidateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        applicationEntity.setJob(jobEntity);
        applicationEntity.setCandidate(userEntity);
        applicationEntity.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(applicationEntity);

        return new CreateApplicationResponse("Application created successfully");
    }
}
