package com.antony.openjobs.modules.users.usecase.publicprofile;

import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.services.PublicProfileLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindPublicProfileUseCase {
    private final PublicProfileLookupService publicProfileLookupService;
    private final IPostRepository postRepository;

    @Transactional(readOnly = true)
    public PublicProfileResponse execute(String username) {
        var user = publicProfileLookupService.findActiveProfile(username);
        return PublicProfileResponse.from(user, postRepository.countByUser_IdAndDeletedAtIsNull(user.getId()));
    }
}
