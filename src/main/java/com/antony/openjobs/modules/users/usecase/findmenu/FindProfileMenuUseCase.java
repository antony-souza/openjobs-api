package com.antony.openjobs.modules.users.usecase.findmenu;

import com.antony.openjobs.modules.rolemenu.repository.IRoleMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindProfileMenuUseCase {
    private final IRoleMenuRepository roleMenuRepository;

    @Transactional(readOnly = true)
    public List<FindProfileMenuResponse> execute(UUID roleId) {
        return roleMenuRepository
                .findByRole_IdAndDeletedAtIsNullAndMenuItem_DeletedAtIsNullAndRole_DeletedAtIsNullOrderByCreatedAtAsc(roleId)
                .stream()
                .map(link -> new FindProfileMenuResponse(
                        link.getMenuItem().getTitle(), link.getMenuItem().getIconName(), link.getMenuItem().getPath()
                ))
                .toList();
    }
}
