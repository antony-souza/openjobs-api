package com.antony.openjobs.modules.jobs.usecase.update;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateJobUseCaseTest {
    @Test
    void rejectsMissingOrAnotherAuthorsJobWithoutSaving() {
        var repository = mock(JobRepository.class);
        assertThatThrownBy(() -> new UpdateJobUseCase(repository).execute(UUID.randomUUID(),
                new UpdateJobRequest("Título", "Descrição"), UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode().value()).isEqualTo(404));
        verify(repository, never()).save(any());
    }

    @Test
    void editsOwnJobAndReturnsUpdateMessage() {
        var repository = mock(JobRepository.class);
        var userId = UUID.randomUUID();
        var jobId = UUID.randomUUID();
        var job = new JobEntity();
        when(repository.findByIdAndPublishedBy_IdAndDeletedAtIsNull(jobId, userId)).thenReturn(Optional.of(job));
        var response = new UpdateJobUseCase(repository).execute(jobId, new UpdateJobRequest(" Título ", " Descrição "), userId);
        assertThat(job.getTitle()).isEqualTo("Título");
        assertThat(job.getDescription()).isEqualTo("Descrição");
        assertThat(response.message()).isEqualTo("Vaga atualizada com sucesso!");
        verify(repository).save(job);
    }
}
