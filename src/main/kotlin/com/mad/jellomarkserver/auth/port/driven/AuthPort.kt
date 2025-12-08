package com.mad.jellomarkserver.auth.port.driven

import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.core.domain.model.AuthEmail

interface AuthPort {
    fun save(auth: Auth): Auth
    fun findByEmail(email: AuthEmail): Auth?
}
