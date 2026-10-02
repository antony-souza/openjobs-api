package com.antony.openjobs.modules.posts.repository.projection;
import java.util.UUID;
public interface PostCount {
    UUID getPostId();
    Long getTotal();
}
