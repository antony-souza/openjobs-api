package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.users.repository.UserRepository;
import com.antony.openjobs.modules.users.usecase.delete.DeleteUserUseCase;
import com.antony.openjobs.modules.users.usecase.findall.FindAllUsersProjection;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserUseCase;
import com.antony.openjobs.services.pagination.PaginationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Test
    void shouldReturnPagedUsersInASuccessfulApiResponse() {
        UserRepository userRepository = mock(UserRepository.class);
        PaginationService paginationService = mock(PaginationService.class);
        UserController controller = new UserController(
                mock(UpdateUserUseCase.class),
                mock(DeleteUserUseCase.class),
                userRepository,
                paginationService
        );
        Pageable pageable = PageRequest.of(0, 10);
        FindAllUsersProjection user = userProjection(
                UUID.randomUUID(),
                "Antony Souza",
                "antony@infojobs.com.br",
                "Administrador"
        );
        IPaginationResponse<FindAllUsersProjection> pagination = new IPaginationResponse<>(
                0,
                10,
                1,
                List.of(user)
        );
        when(paginationService.execute(userRepository, pageable, FindAllUsersProjection.class))
                .thenReturn(pagination);

        var response = controller.findAll(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data()).isEqualTo(pagination);
        assertThat(response.getBody().errors()).isEmpty();
        verify(paginationService).execute(userRepository, pageable, FindAllUsersProjection.class);
    }

    private static FindAllUsersProjection userProjection(UUID id, String name, String email, String roleName) {
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
