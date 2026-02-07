package com.mad.jellomarkserver.reservation.core.domain.exception

class TreatmentNotInShopException(treatmentId: String, shopId: String) : RuntimeException(
    "Treatment $treatmentId does not belong to shop $shopId"
)
