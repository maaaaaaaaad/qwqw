package com.mad.jellomarkserver.beautishop.core.domain.exception

class InvalidShopRegNumException(regNum: String) : RuntimeException("Invalid shop registration number: $regNum")
