package com.antony.openjobs.common.repositories;

import com.antony.openjobs.common.entities.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IBaseRepository<TEntity, TID> extends JpaRepository<TEntity, TID> {
    <P> Page<P> findAllByDeletedAtIsNull(
            Pageable pageable,
            Class<P> projection
    );
}
