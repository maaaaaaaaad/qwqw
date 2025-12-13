package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.RefreshTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, UUID> {
    fun findByEmail(email: String): RefreshTokenJpaEntity?
    fun findByToken(token: String): RefreshTokenJpaEntity?
    fun deleteByEmail(email: String)
}
