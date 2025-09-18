package com.mad.jellomarkserver.member.adapter.out.persistence.repository

import com.mad.jellomarkserver.member.adapter.out.persistence.entity.MemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, UUID> {
    fun existsByEmail(email: String): Boolean
    fun existsByBusinessRegistrationNumber(businessRegistrationNumber: String): Boolean
}