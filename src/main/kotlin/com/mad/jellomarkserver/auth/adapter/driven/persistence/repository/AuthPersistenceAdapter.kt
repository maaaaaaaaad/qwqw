package com.mad.jellomarkserver.auth.adapter.driven.persistence.repository

import com.mad.jellomarkserver.auth.core.domain.model.Auth
import com.mad.jellomarkserver.auth.port.driven.AuthPort
import org.springframework.stereotype.Component

@Component
class AuthPersistenceAdapter : AuthPort {
    override fun save(auth: Auth): Auth {
        throw NotImplementedError("AuthPersistenceAdapter not yet implemented - will be completed in Phase 3")
    }
}
