package com.mad.jellomarkserver.owner.core.domain.exception

class DuplicateOwnerBusinessNumberException(businessNumber: String) :
    RuntimeException(
        "Duplicate business number: $businessNumber"
    ) {
}
