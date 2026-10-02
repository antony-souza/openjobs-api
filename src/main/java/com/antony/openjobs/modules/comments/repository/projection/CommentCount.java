package com.antony.openjobs.modules.comments.repository.projection;

import java.util.UUID;

public interface CommentCount {
    UUID getCommentId();
    Long getTotal();
}
