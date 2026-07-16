package com.mad.jellomarkserver.designer.core.domain.exception

class UnauthorizedDesignerAccessException(val designerId: String) : RuntimeException(
    "Unauthorized access to designer: $designerId"
)
