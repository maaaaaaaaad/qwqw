package com.mad.jellomarkserver.owner.core.domain.exception

class InvalidOwnerEmailException(email: String) : RuntimeException("Invalid owner email: $email")
