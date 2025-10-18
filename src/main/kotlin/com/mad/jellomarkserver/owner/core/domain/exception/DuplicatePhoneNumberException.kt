package com.mad.jellomarkserver.owner.core.domain.exception

class DuplicatePhoneNumberException(phoneNumber: String) :
    RuntimeException(
        "Duplicate phone number: $phoneNumber"
    ) {
}