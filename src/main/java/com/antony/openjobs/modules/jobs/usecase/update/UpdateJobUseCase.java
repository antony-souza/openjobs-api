package com.antony.openjobs.modules.jobs.usecase.create;

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
public class CreateJobUseCase {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public CreateJobResponse execute(CreateJobRequest request, UUID publishedById) {
        if (jobRepository.existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(request.title(), publishedById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vaga já cadastrada");
        }

        UserEntity publishedBy = userRepository.getReferenceById(publishedById);

        JobEntity jobEntity = new JobEntity();

        jobEntity.setTitle(request.title());
        jobEntity.setDescription(request.description());
        jobEntity.setPublishedBy(publishedBy);

        jobRepository.save(jobEntity);

        return new CreateJobResponse("Vaga criada com sucesso!");
    }
}
