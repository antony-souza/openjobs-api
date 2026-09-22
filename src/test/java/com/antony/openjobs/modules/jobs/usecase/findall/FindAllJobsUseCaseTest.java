package com.antony.openjobs.modules.jobs.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllJobsUseCaseTest {

    @Mock
    private JobRepository jobRepository;

    private FindAllJobsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindAllJobsUseCase(jobRepository);
    }

    @Test
    void shouldReturnJobsWithPaginationMetadata() {
        Pageable pageable = PageRequest.of(0, 20);
        FindAllJobsProjection firstJob = job("Backend Java", "Desenvolvimento de APIs", "Antony");
        FindAllJobsProjection secondJob = job("Frontend React", "Desenvolvimento de interfaces", "Maria");
        Page<FindAllJobsProjection> jobsPage = new PageImpl<>(
                List.of(firstJob, secondJob), pageable, 2
        );
        when(jobRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(jobsPage);

        IPaginationResponse<FindAllJobsProjection> response = useCase.execute(pageable);

        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.total()).isEqualTo(2);
        assertThat(response.items()).containsExactly(firstJob, secondJob);
        verify(jobRepository).findAllByDeletedAtIsNull(pageable);
    }

    @Test
    void shouldReturnAnEmptyPageWhenThereAreNoJobs() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<FindAllJobsProjection> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(jobRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(emptyPage);

        IPaginationResponse<FindAllJobsProjection> response = useCase.execute(pageable);

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.total()).isZero();
        assertThat(response.items()).isEmpty();
        verify(jobRepository).findAllByDeletedAtIsNull(pageable);
    }

    private FindAllJobsProjection job(String title, String description, String publishedByName) {
        return new FindAllJobsProjection() {
            @Override
            public UUID getId() {
                return UUID.randomUUID();
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String getPublishedByName() {
                return publishedByName;
            }
        };
    }
}
