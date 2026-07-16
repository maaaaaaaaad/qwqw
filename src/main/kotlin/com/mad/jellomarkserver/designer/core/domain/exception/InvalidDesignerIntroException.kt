package com.mad.jellomarkserver.designer.core.domain.exception

class InvalidDesignerIntroException(val intro: String) : RuntimeException(
    "Invalid designer intro: $intro"
)
