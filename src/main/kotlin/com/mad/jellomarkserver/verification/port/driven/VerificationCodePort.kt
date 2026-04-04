package com.mad.jellomarkserver.verification.port.driven

import com.mad.jellomarkserver.verification.core.domain.model.VerificationCode
import com.mad.jellomarkserver.verification.core.domain.model.VerificationType

interface VerificationCodePort {
    fun save(verificationCode: VerificationCode): VerificationCode
    fun findLatestByTargetAndType(target: String, type: VerificationType): VerificationCode?
    fun countRecentByTarget(target: String, withinMinutes: Long): Long
}
