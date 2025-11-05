package com.mad.jellomarkserver.owner.core.domain.exception

class InvalidOwnerBusinessNumberException(brn: String) :
    RuntimeException(
        "Invalid Business number: $brn"
    )