package com.mad.jellomarkserver.designer.core.domain.exception

class InvalidDesignerNameException(val name: String) : RuntimeException(
    "Invalid designer name: $name"
)
