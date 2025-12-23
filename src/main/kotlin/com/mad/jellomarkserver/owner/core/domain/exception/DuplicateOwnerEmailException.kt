package com.mad.jellomarkserver.owner.core.domain.exception

class DuplicateOwnerEmailException(email: String) : RuntimeException("Duplicate owner email: $email")
