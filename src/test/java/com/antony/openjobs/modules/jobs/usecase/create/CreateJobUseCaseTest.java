package com.antony.openjobs.modules.jobs.usecase.create;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateJobUseCaseTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateJobUseCase createJobUseCase;

    @Captor
    private ArgumentCaptor<JobEntity> jobCaptor;

    @Test
    void shouldCreateJobForAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        CreateJobRequest request = new CreateJobRequest(
                "Desenvolvedor Java",
                "Crie e mantenha serviços Spring Boot."
        );

        when(jobRepository.existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(request.title(), userId))
                .thenReturn(false);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        CreateJobResponse response = createJobUseCase.execute(request, userId);

        assertThat(response.message()).isEqualTo("Vaga criada com sucesso!");
        verify(jobRepository).save(jobCaptor.capture());

        JobEntity savedJob = jobCaptor.getValue();
        assertThat(savedJob.getTitle()).isEqualTo(request.title());
        assertThat(savedJob.getDescription()).isEqualTo(request.description());
        assertThat(savedJob.getPublishedBy()).isSameAs(user);
    }

    @Test
    void shouldRejectJobWhenTitleAlreadyExists() {
        UUID userId = UUID.randomUUID();
        CreateJobRequest request = new CreateJobRequest(
                "Desenvolvedor Java",
                "Crie e mantenha serviços Spring Boot."
        );

        when(jobRepository.existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(request.title(), userId))
                .thenReturn(true);

        assertThatThrownBy(() -> createJobUseCase.execute(request, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Vaga já cadastrada");
                });

        verifyNoInteractions(userRepository);
        verify(jobRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

}
