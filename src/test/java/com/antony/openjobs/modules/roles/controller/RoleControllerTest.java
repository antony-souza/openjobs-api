package com.antony.openjobs.modules.roles.controller;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleRequest;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleUseCase;
import com.antony.openjobs.modules.roles.usecase.findall.FindAllRolesProjection;
import com.antony.openjobs.modules.roles.usecase.findall.FindAllRolesUseCase;
import com.antony.openjobs.modules.roles.usecase.update.UpdateRoleUseCase;
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

class RoleControllerTest {

    @Test
    void shouldReturnPagedRolesInASuccessfulApiResponse() {
        FindAllRolesUseCase findAllRolesUseCase = mock(FindAllRolesUseCase.class);
        RoleController controller = new RoleController(
                findAllRolesUseCase,
                mock(CreateRoleUseCase.class),
                mock(UpdateRoleUseCase.class)
        );
        Pageable pageable = PageRequest.of(1, 10);
        FindAllRolesProjection role = roleProjection(UUID.randomUUID(), "Fundador");
        IPaginationResponse<FindAllRolesProjection> pagination = new IPaginationResponse<>(
                1,
                10,
                21,
                List.of(role)
        );
        when(findAllRolesUseCase.execute(pageable)).thenReturn(pagination);

        var response = controller.findAll(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data()).isEqualTo(pagination);
        assertThat(response.getBody().errors()).isEmpty();
        verify(findAllRolesUseCase).execute(pageable);
    }

    @Test
    void shouldReturnCreatedApiResponse() {
        CreateRoleUseCase useCase = mock(CreateRoleUseCase.class);
        UpdateRoleUseCase updateRoleUseCase = mock(UpdateRoleUseCase.class);
        RoleController controller = new RoleController(
                mock(FindAllRolesUseCase.class),
                useCase,
                updateRoleUseCase
        );
        CreateRoleRequest request = new CreateRoleRequest("Fundador", "founder", 100);
        CreateRoleResponse useCaseResponse = new CreateRoleResponse("Role cadastrada com sucesso!");
        when(useCase.execute(request)).thenReturn(useCaseResponse);

        var response = controller.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data()).isEqualTo(useCaseResponse);
        assertThat(response.getBody().errors()).isEmpty();
        verify(useCase).execute(request);
    }

    private static FindAllRolesProjection roleProjection(UUID id, String name) {
        return new FindAllRolesProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }
}
