package com.mad.jellomarkserver.treatment.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.treatment.core.domain.model.Treatment
import com.mad.jellomarkserver.treatment.core.domain.model.TreatmentId

interface TreatmentPort {
    fun save(treatment: Treatment): Treatment
    fun findById(id: TreatmentId): Treatment?
    fun findByShopId(shopId: ShopId): List<Treatment>
    fun delete(id: TreatmentId)
}
