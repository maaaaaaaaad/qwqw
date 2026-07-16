package com.mad.jellomarkserver.designer.core.domain.exception

class InvalidDesignerPhotosException(val count: Int) : RuntimeException(
    "Invalid designer photos count: $count (maximum 5 allowed)"
)
