package com.mad.jellomarkserver.apigateway.adapter.driving.web.response

import com.mad.jellomarkserver.designer.core.domain.model.Designer
import java.time.Instant
import java.util.*

data class DesignerResponse(
    val id: UUID,
    val shopId: UUID,
    val name: String,
    val nickname: String?,
    val intro: String?,
    val photoUrls: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(designer: Designer): DesignerResponse {
            return DesignerResponse(
                id = designer.id.value,
                shopId = designer.shopId.value,
                name = designer.name.value,
                nickname = designer.nickname?.value,
                intro = designer.intro?.value,
                photoUrls = designer.photoUrls.toStringList(),
                createdAt = designer.createdAt,
                updatedAt = designer.updatedAt
            )
        }
    }
}
