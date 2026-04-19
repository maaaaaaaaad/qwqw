package com.mad.jellomarkserver.member.adapter.driven.persistence.repository

import com.mad.jellomarkserver.member.adapter.driven.persistence.entity.MemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, UUID> {
    fun findBySocialProviderAndSocialIdAndDeletedAtIsNull(socialProvider: String, socialId: String): MemberJpaEntity?
    fun findBySocialIdAndDeletedAtIsNull(socialId: String): MemberJpaEntity?
}
