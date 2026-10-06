package com.antony.openjobs.modules.jobs.usecase.update;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateJobUseCase {
    private final JobRepository jobRepository;

    @Transactional
    public UpdateJobResponse execute(UUID jobId, UpdateJobRequest request, UUID publishedById) {
        JobEntity jobEntity = jobRepository.findByIdAndPublishedBy_IdAndDeletedAtIsNull(jobId, publishedById)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vaga não encontrada"
                ));

        jobEntity.setTitle(request.title().trim());
        jobEntity.setDescription(request.description().trim());

        jobRepository.save(jobEntity);

        return new UpdateJobResponse("Vaga atualizada com sucesso!");
    }
}
