package com.mad.jellomarkserver.auth.port.driven

import com.mad.jellomarkserver.auth.core.domain.model.Auth

fun interface AuthPort {
    fun save(auth: Auth): Auth
}
