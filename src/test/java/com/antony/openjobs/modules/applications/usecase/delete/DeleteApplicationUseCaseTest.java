package com.antony.openjobs.modules.applications.usecase.delete;

import com.antony.openjobs.modules.applications.model.ApplicationEntity;
import com.antony.openjobs.modules.applications.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@ExtendWith(MockitoExtension.class)
class DeleteApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private DeleteApplicationUseCase deleteApplicationUseCase;

    @Test
    void shouldSoftDeleteApplicationOwnedByCandidate() {
        UUID applicationId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        ApplicationEntity application = new ApplicationEntity();
        application.setId(applicationId);

        when(applicationRepository.findByIdAndCandidateIdAndDeletedAtIsNull(applicationId, candidateId))
                .thenReturn(of(application));

        DeleteApplicationResponse response = deleteApplicationUseCase.execute(applicationId, candidateId);

        assertThat(response.message()).isEqualTo("Application deleted successfully");
        assertThat(application.getDeletedAt()).isNotNull();
        verify(applicationRepository).save(application);
    }

    @Test
    void shouldReturnNotFoundWhenApplicationDoesNotExistForCandidate() {
        UUID applicationId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();

        when(applicationRepository.findByIdAndCandidateIdAndDeletedAtIsNull(applicationId, candidateId))
                .thenReturn(empty());

        assertThatThrownBy(() -> deleteApplicationUseCase.execute(applicationId, candidateId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseException.getReason()).isEqualTo("Application not found");
                });

        verify(applicationRepository, never()).save(any(ApplicationEntity.class));
    }
}
