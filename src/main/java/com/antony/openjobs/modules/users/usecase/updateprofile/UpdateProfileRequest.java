package com.antony.openjobs.modules.users.usecase.updateprofile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255) String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 255) String email,

        @NotBlank(message = "O @usuário é obrigatório")
        @Pattern(regexp = "^[a-zA-Z0-9._]{3,30}$", message = "Use de 3 a 30 letras, números, ponto ou _ no @usuário") String username,

        MultipartFile avatar,
        boolean removeAvatar,
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String password
) {
    public UpdateProfileRequest {
        if (password != null && password.isBlank()) {
            password = null;
        }
    }
}
