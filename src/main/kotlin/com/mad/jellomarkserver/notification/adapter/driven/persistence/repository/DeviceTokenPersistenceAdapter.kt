package com.mad.jellomarkserver.notification.adapter.driven.persistence.repository

import com.mad.jellomarkserver.notification.adapter.driven.persistence.mapper.DeviceTokenMapper
import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import com.mad.jellomarkserver.notification.port.driven.DeviceTokenPort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class DeviceTokenPersistenceAdapter(
    private val repository: DeviceTokenJpaRepository,
    private val mapper: DeviceTokenMapper
) : DeviceTokenPort {

    override fun save(deviceToken: DeviceToken): DeviceToken {
        val entity = mapper.toEntity(deviceToken)
        val saved = repository.save(entity)
        return mapper.toDomain(saved)
    }

    @Transactional
    override fun deleteByToken(token: String) {
        repository.deleteByToken(token)
    }

    override fun findByUserIdAndUserRole(userId: UUID, userRole: UserRole): List<DeviceToken> {
        return repository.findByUserIdAndUserRole(userId, userRole.name)
            .map { mapper.toDomain(it) }
    }

    override fun findByToken(token: String): DeviceToken? {
        return repository.findByToken(token)?.let { mapper.toDomain(it) }
    }
}
