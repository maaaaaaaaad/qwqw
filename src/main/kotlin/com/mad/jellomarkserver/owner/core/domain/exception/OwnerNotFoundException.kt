package com.mad.jellomarkserver.owner.core.domain.exception

class OwnerNotFoundException(email: String) : RuntimeException("Owner not found: $email")
