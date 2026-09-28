package com.antony.openjobs.config.upload;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cloudflare.r2", name = "enabled", havingValue = "true")
public class R2FileUploadService implements IFileUploadService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private static final Map<String, String> CONTENT_TYPES_BY_EXTENSION = Map.of(
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".png", "image/png",
            ".gif", "image/gif",
            ".webp", "image/webp");

    private final S3Client s3Client;
    private final CloudflareR2Properties properties;

    @Override
    public String upload(
            MultipartFile file,
            String directory) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione um arquivo para enviar");
        }

        var extension = getExtension(file);
        var key = "%s/%s/%s%s".formatted("openjobs", directory, UUID.randomUUID(), extension);
        var contentType = getContentType(file, extension);

        var request = PutObjectRequest.builder()
                .bucket(properties.bucketName())
                .key(key)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes()));

            return properties.publicUrl().replaceAll("/+$", "") + "/" + key;

        } catch (IOException | S3Exception | SdkClientException | IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível enviar o arquivo para o bucket",
                    exception);
        }
    }

    private String getExtension(MultipartFile file) {

        var originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            return "";
        }

        var lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == originalFilename.length() - 1) {
            return "";
        }

        var extension = originalFilename.substring(lastDotIndex).toLowerCase(Locale.ROOT);

        return extension.matches("\\.[a-z0-9]{1,10}") ? extension : "";
    }

    private String getContentType(
            MultipartFile file,
            String extension) {

        var contentType = file.getContentType();

        if (contentType != null
                && !contentType.isBlank()
                && !DEFAULT_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            return contentType;
        }

        return CONTENT_TYPES_BY_EXTENSION.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }
}
