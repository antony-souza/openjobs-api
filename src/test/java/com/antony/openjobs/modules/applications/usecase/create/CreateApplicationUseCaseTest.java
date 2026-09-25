package com.antony.openjobs.modules.applications.usecase.create;

import com.antony.openjobs.modules.applications.enums.ApplicationStatus;
import com.antony.openjobs.modules.applications.model.ApplicationEntity;
import com.antony.openjobs.modules.applications.repository.ApplicationRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateApplicationUseCase createApplicationUseCase;

    @Captor
    private ArgumentCaptor<ApplicationEntity> applicationCaptor;

    @Test
    void shouldCreateApplicationForCandidateAndJob() {
        UUID candidateId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        CreateApplicationRequest request = new CreateApplicationRequest(jobId);
        UserEntity candidate = new UserEntity();
        JobEntity job = new JobEntity();

        when(applicationRepository.existsByCandidateIdAndJobIdAndDeletedAtIsNull(candidateId, jobId))
                .thenReturn(false);
        when(jobRepository.findByIdAndDeletedAtIsNull(jobId)).thenReturn(Optional.of(job));
        when(userRepository.findByIdAndDeletedAtIsNull(candidateId)).thenReturn(Optional.of(candidate));

        CreateApplicationResponse response = createApplicationUseCase.execute(candidateId, request);

        assertThat(response.message()).isEqualTo("Application created successfully");
        verify(applicationRepository).save(applicationCaptor.capture());

        ApplicationEntity savedApplication = applicationCaptor.getValue();
        assertThat(savedApplication.getCandidate()).isSameAs(candidate);
        assertThat(savedApplication.getJob()).isSameAs(job);
        assertThat(savedApplication.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    void shouldReturnMessageWhenCandidateAlreadyAppliedForJob() {
        UUID candidateId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        CreateApplicationRequest request = new CreateApplicationRequest(jobId);

        when(applicationRepository.existsByCandidateIdAndJobIdAndDeletedAtIsNull(candidateId, jobId))
                .thenReturn(true);

        CreateApplicationResponse response = createApplicationUseCase.execute(candidateId, request);

        assertThat(response.message()).isEqualTo("Candidato já se candidatou para essa vaga");
        verifyNoInteractions(jobRepository, userRepository);
        verify(applicationRepository, never()).save(any(ApplicationEntity.class));
    }

    @Test
    void shouldReturnNotFoundWhenJobDoesNotExist() {
        UUID candidateId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        CreateApplicationRequest request = new CreateApplicationRequest(jobId);

        when(applicationRepository.existsByCandidateIdAndJobIdAndDeletedAtIsNull(candidateId, jobId))
                .thenReturn(false);
        when(jobRepository.findByIdAndDeletedAtIsNull(jobId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createApplicationUseCase.execute(candidateId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseException.getReason()).isEqualTo("Job not found");
                });

        verifyNoInteractions(userRepository);
        verify(applicationRepository, never()).save(any(ApplicationEntity.class));
    }

    @Test
    void shouldReturnNotFoundWhenCandidateDoesNotExist() {
        UUID candidateId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        CreateApplicationRequest request = new CreateApplicationRequest(jobId);

        when(applicationRepository.existsByCandidateIdAndJobIdAndDeletedAtIsNull(candidateId, jobId))
                .thenReturn(false);
        when(jobRepository.findByIdAndDeletedAtIsNull(jobId)).thenReturn(Optional.of(new JobEntity()));
        when(userRepository.findByIdAndDeletedAtIsNull(candidateId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createApplicationUseCase.execute(candidateId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseException.getReason()).isEqualTo("User not found");
                });

        verify(applicationRepository, never()).save(any(ApplicationEntity.class));
    }
}
