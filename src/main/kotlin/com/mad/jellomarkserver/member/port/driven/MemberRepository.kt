package com.mad.jellomarkserver.member.port.driven

import com.mad.jellomarkserver.member.adapter.out.persistence.entity.MemberJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MemberRepository : JpaRepository<MemberJpaEntity, UUID>