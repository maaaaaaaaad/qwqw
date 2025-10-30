package com.mad.jellomarkserver.owner.core.domain.exception

class DuplicateBusinessNumberException(businessNumber: String) :
    RuntimeException(
        "Duplicate business number: $businessNumber"
    ) {
}
