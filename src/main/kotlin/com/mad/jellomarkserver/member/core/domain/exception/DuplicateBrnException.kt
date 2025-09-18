package com.mad.jellomarkserver.member.core.domain.exception

class DuplicateBrnException(brn: String) :
    RuntimeException(
        "Business registration number already in use: $brn"
    ) {
}