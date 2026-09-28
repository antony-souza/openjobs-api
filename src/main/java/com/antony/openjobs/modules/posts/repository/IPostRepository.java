package com.antony.openjobs.modules.posts.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;

@Repository
public interface IPostRepository extends IBaseRepository<PostEntity, UUID> {}
