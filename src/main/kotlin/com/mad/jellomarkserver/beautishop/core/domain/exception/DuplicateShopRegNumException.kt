package com.mad.jellomarkserver.beautishop.core.domain.exception

class DuplicateShopRegNumException(regNum: String) : RuntimeException("Duplicate shop registration number: $regNum")
