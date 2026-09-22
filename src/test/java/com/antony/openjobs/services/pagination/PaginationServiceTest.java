package com.antony.openjobs.services.pagination;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.users.usecase.findall.FindAllUsersProjection;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaginationServiceTest {

    @Mock
    private IBaseRepository<Object, UUID> repository;

    private PaginationService paginationService;

    @BeforeEach
    void setUp() {
        paginationService = new PaginationService();
    }

    @Test
    void shouldReturnProjectionPageWithPaginationMetadata() {
        Pageable pageable = PageRequest.of(0, 20);
        FindAllUsersProjection firstUser = mock(FindAllUsersProjection.class);
        FindAllUsersProjection secondUser = mock(FindAllUsersProjection.class);
        Page<FindAllUsersProjection> page = new PageImpl<>(
                List.of(firstUser, secondUser), pageable, 2
        );
        when(repository.findAllByDeletedAtIsNull(pageable, FindAllUsersProjection.class))
                .thenReturn(page);

        IPaginationResponse<FindAllUsersProjection> response = paginationService.execute(
                repository,
                pageable,
                FindAllUsersProjection.class
        );

        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.total()).isEqualTo(2);
        assertThat(response.items()).containsExactly(firstUser, secondUser);
        verify(repository).findAllByDeletedAtIsNull(pageable, FindAllUsersProjection.class);
    }

    @Test
    void shouldReturnEmptyItemsWhenRepositoryHasNoRecords() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<FindAllUsersProjection> page = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAllByDeletedAtIsNull(pageable, FindAllUsersProjection.class))
                .thenReturn(page);

        IPaginationResponse<FindAllUsersProjection> response = paginationService.execute(
                repository,
                pageable,
                FindAllUsersProjection.class
        );

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.total()).isZero();
        assertThat(response.items()).isEmpty();
        verify(repository).findAllByDeletedAtIsNull(pageable, FindAllUsersProjection.class);
    }
}
