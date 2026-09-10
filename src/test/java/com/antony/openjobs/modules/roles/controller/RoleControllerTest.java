package com.antony.openjobs.modules.roles.controller;

import com.antony.openjobs.modules.roles.usecase.create.CreateRoleRequest;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleControllerTest {

    @Test
    void shouldReturnCreatedApiResponse() {
        CreateRoleUseCase useCase = mock(CreateRoleUseCase.class);
        RoleController controller = new RoleController(useCase);
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
}
