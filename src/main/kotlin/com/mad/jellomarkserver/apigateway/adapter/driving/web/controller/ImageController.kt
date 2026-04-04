package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ImageUploadResponse
import com.mad.jellomarkserver.image.port.driving.UploadImageCommand
import com.mad.jellomarkserver.image.port.driving.UploadImageUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ImageController(
    private val uploadImageUseCase: UploadImageUseCase
) {

    @PostMapping("/api/images/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadImage(@RequestParam("file") file: MultipartFile): ImageUploadResponse {
        val url = uploadImageUseCase.execute(
            UploadImageCommand(
                inputStream = file.inputStream,
                contentType = file.contentType ?: "application/octet-stream",
                originalFilename = file.originalFilename ?: "unknown",
                sizeBytes = file.size
            )
        )
        return ImageUploadResponse(url = url)
    }
}
