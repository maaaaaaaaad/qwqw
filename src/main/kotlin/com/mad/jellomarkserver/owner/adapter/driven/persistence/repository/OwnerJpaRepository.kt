package com.mad.jellomarkserver.owner.adapter.driven.persistence.repository

import com.mad.jellomarkserver.owner.adapter.driven.persistence.entity.OwnerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OwnerJpaRepository : JpaRepository<OwnerJpaEntity, UUID> {}
