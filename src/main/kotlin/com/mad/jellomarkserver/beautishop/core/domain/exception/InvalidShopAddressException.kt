package com.mad.jellomarkserver.beautishop.core.domain.exception

class InvalidShopAddressException(address: String) : RuntimeException("Invalid shop address: $address")
