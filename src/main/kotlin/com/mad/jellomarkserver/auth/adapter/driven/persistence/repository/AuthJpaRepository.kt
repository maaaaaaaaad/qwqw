package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.adapter.driven.persistence.entity.AuthJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuthJpaRepository : JpaRepository<AuthJpaEntity, UUID> {
    fun findByEmail(email: String): AuthJpaEntity?
}
