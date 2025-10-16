package com.mad.jellomarkserver.owner.core.domain.exception

class InvalidPhoneNumberException(phoneNumber: String) :
    RuntimeException(
        "Invalid phone number: $phoneNumber"
    ) {
}