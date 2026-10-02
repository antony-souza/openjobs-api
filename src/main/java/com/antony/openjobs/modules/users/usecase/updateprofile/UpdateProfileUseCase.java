package com.antony.openjobs.modules.users.usecase.updateprofile;

import com.antony.openjobs.config.upload.IFileUploadService;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import com.antony.openjobs.modules.users.usecase.ProfileResponse;
import com.antony.openjobs.utils.PublicProfileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase {
    private final IUserRepository userRepository;
    private final IFileUploadService fileUploadService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ProfileResponse execute(UUID userId, UpdateProfileRequest request) {
        var user = findUser(userId);
        var email = request.email().trim().toLowerCase(Locale.ROOT);
        var username = request.username().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCaseAndIdNotAndDeletedAtIsNull(email, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado");
        }
        if (userRepository.existsByUsernameIgnoreCaseAndIdNotAndDeletedAtIsNull(username, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este @usuário já está cadastrado");
        }
        var portfolioUrl = PublicProfileUtils.cleanUrl(request.portfolioUrl());
        var linkedinUrl = PublicProfileUtils.cleanUrl(request.linkedinUrl());
        if (request.avatar() != null && !request.avatar().isEmpty()) {
            user.setAvatarUrl(uploadImage(request.avatar(), "avatars/" + userId));
        } else if (request.removeAvatar()) {
            user.setAvatarUrl(null);
        }
        if (request.cover() != null && !request.cover().isEmpty()) {
            user.setCoverUrl(uploadImage(request.cover(), "covers/" + userId));
        } else if (request.removeCover()) {
            user.setCoverUrl(null);
        }
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setUsername(username);
        user.setHeadline(PublicProfileUtils.cleanText(request.headline()));
        user.setBio(PublicProfileUtils.cleanText(request.bio()));
        user.setLocation(PublicProfileUtils.cleanText(request.location()));
        user.setPortfolioUrl(portfolioUrl);
        user.setLinkedinUrl(linkedinUrl);
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return ProfileResponse.from(userRepository.save(user));
    }

    private String uploadImage(MultipartFile file, String folder) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A foto deve ter no máximo 5 MB");
        }
        var contentType = file.getContentType() == null ? "" : file.getContentType();
        if (!Set.of("image/jpeg", "image/png", "image/gif").contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use uma foto JPG, PNG ou GIF");
        }
        try (var input = file.getInputStream(); var stream = ImageIO.createImageInputStream(input)) {
            var readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo não é uma imagem válida");
            }
            var reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                var format = reader.getFormatName().toLowerCase(Locale.ROOT);
                var expectedType = "image/" + format;
                if (!expectedType.equals(file.getContentType()) || reader.getWidth(0) > 8000 || reader.getHeight(0) > 8000) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem é inválida ou excede 8000 pixels");
                }
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler a foto", exception);
        }
        return fileUploadService.upload(file, folder);
    }

    private UserEntity findUser(UUID id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}
