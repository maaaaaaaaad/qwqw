package com.mad.jellomarkserver.owner.core.domain.exception

class InvalidOwnerPhoneNumberException(phoneNumber: String) :
    RuntimeException(
        "Invalid phone number: $phoneNumber"
    ) {
}