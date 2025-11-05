package com.mad.jellomarkserver.owner.core.domain.exception

class DuplicateOwnerPhoneNumberException(phoneNumber: String) :
    RuntimeException(
        "Duplicate phone number: $phoneNumber"
    ) {
}