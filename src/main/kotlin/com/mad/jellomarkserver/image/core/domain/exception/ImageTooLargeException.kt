package com.mad.jellomarkserver.image.core.domain.exception

class ImageTooLargeException(sizeBytes: Long) :
    RuntimeException("Image size ${sizeBytes / 1024 / 1024}MB exceeds the maximum allowed size of 5MB")
