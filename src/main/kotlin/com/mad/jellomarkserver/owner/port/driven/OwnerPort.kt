package com.mad.jellomarkserver.owner.port.driven

import com.mad.jellomarkserver.owner.core.domain.model.Owner

interface OwnerPort {
    fun save(owner: Owner): Owner
}