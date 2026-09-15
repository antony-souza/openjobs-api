package com.antony.openjobs.modules.jobs.usecase.delete;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteJobUseCase {
    private final JobRepository jobRepository;

    @Transactional
    public DeleteJobResponse execute(UUID jobId, UUID publishedById) {
        JobEntity jobEntity = jobRepository
                .findByIdAndPublishedBy_IdAndDeletedAtIsNull(jobId, publishedById)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        jobEntity.setDeletedAt(LocalDateTime.now());

        return new DeleteJobResponse("Vaga apagada com sucesso");
    }
}
