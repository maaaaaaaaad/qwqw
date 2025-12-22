package com.mad.jellomarkserver.owner.port.driven

import com.mad.jellomarkserver.owner.core.domain.model.Owner
import com.mad.jellomarkserver.owner.core.domain.model.OwnerEmail

interface OwnerPort {
    fun save(owner: Owner): Owner
    fun findByEmail(email: OwnerEmail): Owner?
}