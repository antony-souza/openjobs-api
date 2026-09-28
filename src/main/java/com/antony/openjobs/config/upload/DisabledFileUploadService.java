package com.antony.openjobs.config.upload;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@ConditionalOnProperty(
        prefix = "cloudflare.r2",
        name = "enabled",
        havingValue = "false",
        matchIfMissing = true)
public class DisabledFileUploadService implements IFileUploadService {

    @Override
    public String upload(MultipartFile file, String directory) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "O upload de arquivos não está configurado");
    }
}
