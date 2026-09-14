package com.antony.openjobs.modules.jobs.usecase.update;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateJobUseCase {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public UpdateJobResponse execute(UUID jobId, UpdateJobRequest request, UUID publishedById) {
        JobEntity jobEntity = jobRepository.findByIdAndPublishedBy_IdAndDeletedAtIsNull(jobId, publishedById)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"
                ));

        UserEntity publishedBy = userRepository.getReferenceById(publishedById);

        jobEntity.setTitle(request.title());
        jobEntity.setDescription(request.description());
        jobEntity.setPublishedBy(publishedBy);

        jobRepository.save(jobEntity);

        return new UpdateJobResponse("Vaga criada com sucesso!");
    }
}
