package com.mad.jellomarkserver.image.core.domain.exception

class InvalidImageFormatException(contentType: String) :
    RuntimeException("Unsupported image format: $contentType. Only JPEG, PNG, and WebP are allowed")
