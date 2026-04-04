package com.mad.jellomarkserver.image.core.domain.exception

class ImageUploadFailedException(cause: Throwable) :
    RuntimeException("Failed to upload image", cause)
