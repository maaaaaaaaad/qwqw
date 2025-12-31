package com.mad.jellomarkserver.beautishop.core.domain.exception

class BeautishopNotFoundException(shopId: String) : RuntimeException("Beautishop not found: $shopId")
