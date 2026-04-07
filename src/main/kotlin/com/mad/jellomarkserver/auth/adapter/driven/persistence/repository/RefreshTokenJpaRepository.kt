package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, UUID> {
    fun findByIdentifier(identifier: String): RefreshTokenJpaEntity?
    fun findByToken(token: String): RefreshTokenJpaEntity?

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.identifier = :identifier")
    fun deleteByIdentifier(identifier: String)
}
