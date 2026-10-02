package com.antony.openjobs.modules.users.usecase.updateprofile;

import com.antony.openjobs.config.upload.IFileUploadService;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseTest {
    @Mock IUserRepository users;
    @Mock IFileUploadService uploads;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UpdateProfileUseCase useCase;

    private UserEntity user() {
        var user = new UserEntity(); user.setId(UUID.randomUUID()); user.setName("Maria"); user.setEmail("maria@example.com");
        user.setUsername("maria"); user.setPassword("encoded"); user.setAvatarUrl("https://example.com/old.png");
        var role = new RoleEntity(); role.setName("Candidato"); user.setRole(role); return user;
    }

    @Test void uploadsPhotoAndUpdatesOnlyOwnProfileFields() throws Exception {
        var user = user(); var role = user.getRole();
        var bytes = new ByteArrayOutputStream(); ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", bytes);
        var file = new MockMultipartFile("avatar", "photo.png", "image/png", bytes.toByteArray());
        when(users.findByIdAndDeletedAtIsNull(user.getId())).thenReturn(Optional.of(user));
        when(uploads.upload(file, "avatars/" + user.getId())).thenReturn("https://example.com/new.png");
        when(users.save(user)).thenReturn(user);
        var result = useCase.execute(user.getId(), new UpdateProfileRequest("  Maria Silva  ", "MARIA@example.com", "Maria.Silva", file, false, null));
        assertThat(result.name()).isEqualTo("Maria Silva"); assertThat(result.username()).isEqualTo("maria.silva");
        assertThat(result.avatarUrl()).isEqualTo("https://example.com/new.png"); assertThat(user.getRole()).isSameAs(role);
        assertThat(user.getPassword()).isEqualTo("encoded");
    }

    @Test void rejectsFileThatClaimsToBeAnImageWithoutUploading() {
        var user = user(); when(users.findByIdAndDeletedAtIsNull(user.getId())).thenReturn(Optional.of(user));
        var file = new MockMultipartFile("avatar", "fake.png", "image/png", "not an image".getBytes());
        assertThatThrownBy(() -> useCase.execute(user.getId(), new UpdateProfileRequest("Maria", "maria@example.com", "maria", file, false, null))).isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(uploads); verify(users, never()).save(any());
    }

    @Test void rejectsDuplicateUsernameBeforeUploading() {
        var user = user(); when(users.findByIdAndDeletedAtIsNull(user.getId())).thenReturn(Optional.of(user));
        when(users.existsByUsernameIgnoreCaseAndIdNotAndDeletedAtIsNull("taken", user.getId())).thenReturn(true);
        assertThatThrownBy(() -> useCase.execute(user.getId(), new UpdateProfileRequest("Maria", "maria@example.com", "taken", null, false, null))).isInstanceOf(ResponseStatusException.class);
        verifyNoInteractions(uploads); verify(users, never()).save(any());
    }

    @Test void removesPhotoWithoutChangingRole() {
        var user = user(); when(users.findByIdAndDeletedAtIsNull(user.getId())).thenReturn(Optional.of(user)); when(users.save(user)).thenReturn(user);
        var result = useCase.execute(user.getId(), new UpdateProfileRequest("Maria", "maria@example.com", "maria", null, true, null));
        assertThat(result.avatarUrl()).isNull(); assertThat(result.role()).isEqualTo("Candidato"); verifyNoInteractions(uploads);
    }

    @Test void blankPasswordKeepsExistingHash() {
        var user = user();
        when(users.findByIdAndDeletedAtIsNull(user.getId())).thenReturn(Optional.of(user));
        when(users.save(user)).thenReturn(user);

        for (var password : new String[]{"", "   "}) {
            useCase.execute(user.getId(), new UpdateProfileRequest("Maria", "maria@example.com", "maria", null, false, password));
            assertThat(user.getPassword()).isEqualTo("encoded");
        }

        verifyNoInteractions(passwordEncoder);
    }

    @Test void suppliedPasswordIsEncodedBeforeSaving() {
        var user = user();
        when(users.findByIdAndDeletedAtIsNull(user.getId())).thenReturn(Optional.of(user));
        when(users.save(user)).thenReturn(user);
        when(passwordEncoder.encode("nova-senha-teste")).thenReturn("new-encoded-hash");

        useCase.execute(user.getId(), new UpdateProfileRequest("Maria", "maria@example.com", "maria", null, false, "nova-senha-teste"));

        assertThat(user.getPassword()).isEqualTo("new-encoded-hash");
        verify(passwordEncoder).encode("nova-senha-teste");
    }
}
