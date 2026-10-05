package com.antony.openjobs.modules.jobs.usecase.create;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateJobUseCase {
    private final JobRepository jobRepository;
    private final IUserRepository userRepository;

    @Transactional
    public CreateJobResponse execute(CreateJobRequest request, UUID publishedById) {
        var title = request.title().trim();
        if (jobRepository.existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(title, publishedById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vaga já cadastrada");
        }

        UserEntity publishedBy = userRepository.getReferenceById(publishedById);

        JobEntity jobEntity = new JobEntity();

        jobEntity.setTitle(title);
        jobEntity.setDescription(request.description().trim());
        jobEntity.setPublishedBy(publishedBy);

        jobRepository.save(jobEntity);

        return new CreateJobResponse("Vaga criada com sucesso!");
    }
}
