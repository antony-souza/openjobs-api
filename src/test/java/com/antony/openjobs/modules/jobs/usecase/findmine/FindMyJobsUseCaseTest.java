package com.antony.openjobs.modules.jobs.usecase.findmine;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindMyJobsUseCaseTest {
    @Test
    void scopesListingToAuthenticatedAuthorAndLimitsPageSize() {
        var repository = mock(JobRepository.class);
        var userId = UUID.randomUUID();
        var job = new JobEntity();
        job.setId(UUID.randomUUID());
        job.setTitle("Desenvolvedor");
        job.setDescription("Descrição");
        job.setCreatedAt(LocalDateTime.of(2026, 10, 6, 12, 0));
        when(repository.findByPublishedBy_IdAndDeletedAtIsNull(eq(userId), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable page = invocation.getArgument(1);
                    assertThat(page.getPageNumber()).isZero();
                    assertThat(page.getPageSize()).isEqualTo(30);
                    assertThat(page.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
                    return new PageImpl<>(List.of(job), page, 1);
                });
        var response = new FindMyJobsUseCase(repository).execute(userId, -1, 1000);
        assertThat(response.total()).isEqualTo(1);
        assertThat(response.items().get(0).id()).isEqualTo(job.getId());
        assertThat(response.items().get(0).createdAt()).isNotNull();
    }
}

