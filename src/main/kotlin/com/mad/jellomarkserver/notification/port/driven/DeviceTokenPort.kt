package com.mad.jellomarkserver.notification.port.driven

import com.mad.jellomarkserver.notification.core.domain.model.DeviceToken
import com.mad.jellomarkserver.notification.core.domain.model.UserRole
import java.util.*

interface DeviceTokenPort {
    fun save(deviceToken: DeviceToken): DeviceToken
    fun deleteByToken(token: String)
    fun findByUserIdAndUserRole(userId: UUID, userRole: UserRole): List<DeviceToken>
    fun findByToken(token: String): DeviceToken?
}
