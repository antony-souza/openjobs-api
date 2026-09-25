package com.antony.openjobs.modules.applications.usecase.findall;

import com.antony.openjobs.modules.applications.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllApplicationsByCandidateIdUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private FindAllApplicationsByCandidateIdUseCase findAllApplicationsByCandidateIdUseCase;

    @Test
    void shouldReturnCandidateApplicationsWithPaginationMetadata() {
        UUID candidateId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(1, 10);
        FindAllApplicationsByCandidateIdProjection firstApplication =
                mock(FindAllApplicationsByCandidateIdProjection.class);
        FindAllApplicationsByCandidateIdProjection secondApplication =
                mock(FindAllApplicationsByCandidateIdProjection.class);
        Page<FindAllApplicationsByCandidateIdProjection> page = new PageImpl<>(
                List.of(firstApplication, secondApplication),
                pageable,
                12
        );

        when(applicationRepository.findAllByCandidate_IdAndDeletedAtIsNull(candidateId, pageable))
                .thenReturn(page);

        var response = findAllApplicationsByCandidateIdUseCase.execute(candidateId, pageable);

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.total()).isEqualTo(12);
        assertThat(response.items()).containsExactly(firstApplication, secondApplication);
        verify(applicationRepository).findAllByCandidate_IdAndDeletedAtIsNull(candidateId, pageable);
    }
}
