package com.mad.jellomarkserver.owner.core.domain.exception

class InvalidBusinessNumberException(brn: String) :
    RuntimeException(
        "Invalid Business number: $brn"
    )