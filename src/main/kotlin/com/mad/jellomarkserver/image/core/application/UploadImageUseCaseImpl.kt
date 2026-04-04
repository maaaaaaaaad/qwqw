package com.mad.jellomarkserver.image.core.application

import com.mad.jellomarkserver.image.core.domain.exception.ImageTooLargeException
import com.mad.jellomarkserver.image.core.domain.exception.ImageUploadFailedException
import com.mad.jellomarkserver.image.core.domain.exception.InvalidImageFormatException
import com.mad.jellomarkserver.image.port.driven.ImageStoragePort
import com.mad.jellomarkserver.image.port.driving.UploadImageCommand
import com.mad.jellomarkserver.image.port.driving.UploadImageUseCase
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.imageio.ImageIO

@Service
class UploadImageUseCaseImpl(
    private val imageStoragePort: ImageStoragePort
) : UploadImageUseCase {

    override fun execute(command: UploadImageCommand): String {
        validateFormat(command.contentType)
        validateSize(command.sizeBytes)

        val extension = CONTENT_TYPE_TO_EXT[command.contentType] ?: "jpg"
        val key = "$IMAGE_PREFIX${UUID.randomUUID()}.$extension"

        val resizedStream = resizeIfNeeded(command.inputStream.readAllBytes(), extension)

        return try {
            imageStoragePort.upload(
                key = key,
                inputStream = ByteArrayInputStream(resizedStream),
                contentType = command.contentType,
                sizeBytes = resizedStream.size.toLong()
            )
        } catch (e: Exception) {
            throw ImageUploadFailedException(e)
        }
    }

    private fun validateFormat(contentType: String) {
        if (contentType !in ALLOWED_CONTENT_TYPES) {
            throw InvalidImageFormatException(contentType)
        }
    }

    private fun validateSize(sizeBytes: Long) {
        if (sizeBytes > MAX_FILE_SIZE_BYTES) {
            throw ImageTooLargeException(sizeBytes)
        }
    }

    private fun resizeIfNeeded(imageBytes: ByteArray, extension: String): ByteArray {
        val image = ImageIO.read(ByteArrayInputStream(imageBytes)) ?: return imageBytes

        val longestEdge = maxOf(image.width, image.height)
        if (longestEdge <= MAX_DIMENSION) return imageBytes

        val scale = MAX_DIMENSION.toDouble() / longestEdge
        val newWidth = (image.width * scale).toInt()
        val newHeight = (image.height * scale).toInt()

        val resized = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = resized.createGraphics()
        graphics.drawImage(image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null)
        graphics.dispose()

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(resized, extension, outputStream)
        return outputStream.toByteArray()
    }

    companion object {
        private const val IMAGE_PREFIX = "shop-images/"
        private const val MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024
        private const val MAX_DIMENSION = 1024
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
        private val CONTENT_TYPE_TO_EXT = mapOf(
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp"
        )
    }
}
