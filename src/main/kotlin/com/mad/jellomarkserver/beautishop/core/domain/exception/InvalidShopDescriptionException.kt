package com.mad.jellomarkserver.beautishop.core.domain.exception

class InvalidShopDescriptionException(description: String) : RuntimeException("Invalid shop description: $description")
