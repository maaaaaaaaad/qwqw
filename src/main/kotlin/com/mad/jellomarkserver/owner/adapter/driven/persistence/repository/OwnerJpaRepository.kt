package com.mad.jellomarkserver.owner.adapter.driven.persistence.repository

import com.mad.jellomarkserver.owner.adapter.driven.persistence.entity.OwnerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OwnerJpaRepository : JpaRepository<OwnerJpaEntity, UUID> {
    fun findByEmail(email: String): OwnerJpaEntity?
}
