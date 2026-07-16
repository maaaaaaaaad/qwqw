package com.mad.jellomarkserver.designer.core.domain.exception

class DesignerNotFoundException(val designerId: String) : RuntimeException(
    "Designer not found: $designerId"
)
