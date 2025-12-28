package com.mad.jellomarkserver.beautishop.core.domain.exception

class InvalidShopPhoneNumberException(phoneNumber: String) : RuntimeException("Invalid shop phone number: $phoneNumber")
