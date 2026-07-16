package com.mad.jellomarkserver.designer.port.driven

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.designer.core.domain.model.Designer
import com.mad.jellomarkserver.designer.core.domain.model.DesignerId

interface DesignerPort {
    fun save(designer: Designer): Designer
    fun findById(id: DesignerId): Designer?
    fun findByIds(ids: List<DesignerId>): List<Designer>
    fun findByShopId(shopId: ShopId): List<Designer>
    fun delete(id: DesignerId)
    fun deleteAllByShopId(shopId: ShopId)
}
