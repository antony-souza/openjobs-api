package com.antony.openjobs.modules.users.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllUsersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @Test
    void shouldReturnProjectedUsersWithPaginationMetadata() {
        Pageable pageable = PageRequest.of(1, 10);
        FindAllUsersProjection user = userProjection(
                UUID.randomUUID(),
                "Antony Souza",
                "antony@infojobs.com.br",
                "Administrador"
        );

        when(userRepository.findAllByDeletedAtIsNull(pageable))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 21));

        IPaginationResponse<FindAllUsersProjection> response = findAllUsersUseCase.execute(pageable);

        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.total()).isEqualTo(21);
        assertThat(response.items()).containsExactly(user);
        verify(userRepository).findAllByDeletedAtIsNull(pageable);
    }

    @Test
    void shouldReturnAnEmptyPageWhenThereAreNoUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findAllByDeletedAtIsNull(pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        IPaginationResponse<FindAllUsersProjection> response = findAllUsersUseCase.execute(pageable);

        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.total()).isZero();
        assertThat(response.items()).isEmpty();
        verify(userRepository).findAllByDeletedAtIsNull(pageable);
    }

    private FindAllUsersProjection userProjection(UUID id, String name, String email, String roleName) {
        return new FindAllUsersProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getEmail() {
                return email;
            }

            @Override
            public String getRoleName() {
                return roleName;
            }
        };
    }
}
