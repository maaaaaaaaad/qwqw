package com.mad.jellomarkserver.apigateway.adapter.driving.web.handler

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.auth.core.domain.exception.*
import com.mad.jellomarkserver.beautishop.core.domain.exception.*
import com.mad.jellomarkserver.category.core.domain.exception.CategoryNotFoundException
import com.mad.jellomarkserver.category.core.domain.exception.UnauthorizedShopAccessException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateSocialAccountException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.owner.core.domain.exception.*
import com.mad.jellomarkserver.reservation.core.domain.exception.*
import com.mad.jellomarkserver.review.core.domain.exception.*
import com.mad.jellomarkserver.treatment.core.domain.exception.*
import com.mad.jellomarkserver.image.core.domain.exception.*
import com.mad.jellomarkserver.verification.core.domain.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.mad.jellomarkserver.apigateway"])
class ApiGatewayExceptionHandler {

    private val log = LoggerFactory.getLogger(ApiGatewayExceptionHandler::class.java)

    private fun toErrorCode(ex: Exception): String {
        return ex::class.simpleName
            ?.removeSuffix("Exception")
            ?.replace(Regex("([a-z])([A-Z])"), "$1_$2")
            ?.replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2")
            ?.uppercase()
            ?: "UNKNOWN"
    }

    private fun withCode(status: HttpStatus, ex: Exception): ProblemDetail {
        val pd = ProblemDetail.forStatusAndDetail(status, ex.message)
        pd.setProperty("code", toErrorCode(ex))
        return pd
    }

    @ExceptionHandler(AuthenticationFailedException::class)
    fun handleAuthenticationFailed(ex: AuthenticationFailedException): ProblemDetail {
        return withCode(HttpStatus.UNAUTHORIZED, ex)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ProblemDetail {
        return withCode(HttpStatus.UNAUTHORIZED, ex)
    }

    @ExceptionHandler(InvalidKakaoTokenException::class)
    fun handleInvalidKakaoToken(ex: InvalidKakaoTokenException): ProblemDetail {
        return withCode(HttpStatus.UNAUTHORIZED, ex)
    }

    @ExceptionHandler(KakaoApiException::class)
    fun handleKakaoApiException(ex: KakaoApiException): ProblemDetail {
        return withCode(HttpStatus.BAD_GATEWAY, ex)
    }

    @ExceptionHandler(InvalidAuthEmailException::class)
    fun handleInvalidAuthEmail(ex: InvalidAuthEmailException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(DuplicateAuthEmailException::class)
    fun handleDuplicateAuthEmail(ex: DuplicateAuthEmailException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidRawPasswordException::class)
    fun handleInvalidPassword(ex: InvalidRawPasswordException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(DuplicateSocialAccountException::class)
    fun handleDuplicateSocialAccount(ex: DuplicateSocialAccountException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(DuplicateMemberNicknameException::class)
    fun handleDuplicateMemberNickname(ex: DuplicateMemberNicknameException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidMemberNicknameException::class)
    fun handleInvalidMemberNickname(ex: InvalidMemberNicknameException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(MemberNotFoundException::class)
    fun handleMemberNotFound(ex: MemberNotFoundException): ProblemDetail {
        log.warn("MemberNotFound: {}", ex.message)
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(DuplicateOwnerNicknameException::class)
    fun handleDuplicateOwnerNickname(ex: DuplicateOwnerNicknameException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidOwnerNicknameException::class)
    fun handleInvalidOwnerNickname(ex: InvalidOwnerNicknameException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(DuplicateOwnerBusinessNumberException::class)
    fun handleDuplicateOwnerBusinessNumber(ex: DuplicateOwnerBusinessNumberException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidOwnerBusinessNumberException::class)
    fun handleInvalidOwnerBusinessNumber(ex: InvalidOwnerBusinessNumberException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(DuplicateOwnerPhoneNumberException::class)
    fun handleDuplicateOwnerPhoneNumber(ex: DuplicateOwnerPhoneNumberException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidOwnerPhoneNumberException::class)
    fun handleInvalidOwnerPhoneNumber(ex: InvalidOwnerPhoneNumberException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(DuplicateOwnerEmailException::class)
    fun handleDuplicateOwnerEmail(ex: DuplicateOwnerEmailException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidOwnerEmailException::class)
    fun handleInvalidOwnerEmail(ex: InvalidOwnerEmailException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidShopNameException::class)
    fun handleInvalidShopName(ex: InvalidShopNameException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidShopRegNumException::class)
    fun handleInvalidShopRegNum(ex: InvalidShopRegNumException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(DuplicateShopRegNumException::class)
    fun handleDuplicateShopRegNum(ex: DuplicateShopRegNumException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(InvalidShopPhoneNumberException::class)
    fun handleInvalidShopPhoneNumber(ex: InvalidShopPhoneNumberException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidShopAddressException::class)
    fun handleInvalidShopAddress(ex: InvalidShopAddressException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidShopGPSException::class)
    fun handleInvalidShopGPS(ex: InvalidShopGPSException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidOperatingTimeException::class)
    fun handleInvalidOperatingTime(ex: InvalidOperatingTimeException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidShopDescriptionException::class)
    fun handleInvalidShopDescription(ex: InvalidShopDescriptionException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidShopImageException::class)
    fun handleInvalidShopImage(ex: InvalidShopImageException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(BeautishopNotFoundException::class)
    fun handleBeautishopNotFound(ex: BeautishopNotFoundException): ProblemDetail {
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(InvalidReviewRatingException::class)
    fun handleInvalidReviewRating(ex: InvalidReviewRatingException): ProblemDetail {
        log.warn("InvalidReviewRating: {}", ex.message)
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidReviewContentException::class)
    fun handleInvalidReviewContent(ex: InvalidReviewContentException): ProblemDetail {
        log.warn("InvalidReviewContent: {}", ex.message)
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidReviewImagesException::class)
    fun handleInvalidReviewImages(ex: InvalidReviewImagesException): ProblemDetail {
        log.warn("InvalidReviewImages: {}", ex.message)
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException): ProblemDetail {
        log.warn("ReviewNotFound: {}", ex.message)
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(InvalidReplyContentException::class)
    fun handleInvalidReplyContent(ex: InvalidReplyContentException): ProblemDetail {
        log.warn("InvalidReplyContent: {}", ex.message)
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(UnauthorizedReviewAccessException::class)
    fun handleUnauthorizedReviewAccess(ex: UnauthorizedReviewAccessException): ProblemDetail {
        log.warn("UnauthorizedReviewAccess: {}", ex.message)
        return withCode(HttpStatus.FORBIDDEN, ex)
    }

    @ExceptionHandler(DuplicateReviewException::class)
    fun handleDuplicateReview(ex: DuplicateReviewException): ProblemDetail {
        log.warn("DuplicateReview: {}", ex.message)
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(EmptyReviewException::class)
    fun handleEmptyReview(ex: EmptyReviewException): ProblemDetail {
        log.warn("EmptyReview: {}", ex.message)
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(CategoryNotFoundException::class)
    fun handleCategoryNotFound(ex: CategoryNotFoundException): ProblemDetail {
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(UnauthorizedShopAccessException::class)
    fun handleUnauthorizedShopAccess(ex: UnauthorizedShopAccessException): ProblemDetail {
        return withCode(HttpStatus.FORBIDDEN, ex)
    }

    @ExceptionHandler(UnauthorizedBeautishopAccessException::class)
    fun handleUnauthorizedBeautishopAccess(ex: UnauthorizedBeautishopAccessException): ProblemDetail {
        return withCode(HttpStatus.FORBIDDEN, ex)
    }

    @ExceptionHandler(InvalidTreatmentNameException::class)
    fun handleInvalidTreatmentName(ex: InvalidTreatmentNameException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidTreatmentPriceException::class)
    fun handleInvalidTreatmentPrice(ex: InvalidTreatmentPriceException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidTreatmentDurationException::class)
    fun handleInvalidTreatmentDuration(ex: InvalidTreatmentDurationException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidTreatmentDescriptionException::class)
    fun handleInvalidTreatmentDescription(ex: InvalidTreatmentDescriptionException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(TreatmentNotFoundException::class)
    fun handleTreatmentNotFound(ex: TreatmentNotFoundException): ProblemDetail {
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(UnauthorizedTreatmentAccessException::class)
    fun handleUnauthorizedTreatmentAccess(ex: UnauthorizedTreatmentAccessException): ProblemDetail {
        return withCode(HttpStatus.FORBIDDEN, ex)
    }

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleReservationNotFound(ex: ReservationNotFoundException): ProblemDetail {
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(InvalidReservationStatusTransitionException::class)
    fun handleInvalidReservationStatusTransition(ex: InvalidReservationStatusTransitionException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(ReservationTimeConflictException::class)
    fun handleReservationTimeConflict(ex: ReservationTimeConflictException): ProblemDetail {
        return withCode(HttpStatus.CONFLICT, ex)
    }

    @ExceptionHandler(PastReservationException::class)
    fun handlePastReservation(ex: PastReservationException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(TreatmentNotInShopException::class)
    fun handleTreatmentNotInShop(ex: TreatmentNotInShopException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(UnauthorizedReservationAccessException::class)
    fun handleUnauthorizedReservationAccess(ex: UnauthorizedReservationAccessException): ProblemDetail {
        return withCode(HttpStatus.FORBIDDEN, ex)
    }

    @ExceptionHandler(InvalidReservationMemoException::class)
    fun handleInvalidReservationMemo(ex: InvalidReservationMemoException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidRejectionReasonException::class)
    fun handleInvalidRejectionReason(ex: InvalidRejectionReasonException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(InvalidImageFormatException::class)
    fun handleInvalidImageFormat(ex: InvalidImageFormatException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(ImageTooLargeException::class)
    fun handleImageTooLarge(ex: ImageTooLargeException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(ImageUploadFailedException::class)
    fun handleImageUploadFailed(ex: ImageUploadFailedException): ProblemDetail {
        log.error("Image upload failed", ex)
        return withCode(HttpStatus.INTERNAL_SERVER_ERROR, ex)
    }

    @ExceptionHandler(InvalidVerificationCodeException::class)
    fun handleInvalidVerificationCode(ex: InvalidVerificationCodeException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(VerificationCodeExpiredException::class)
    fun handleVerificationCodeExpired(ex: VerificationCodeExpiredException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(VerificationCodeNotFoundException::class)
    fun handleVerificationCodeNotFound(ex: VerificationCodeNotFoundException): ProblemDetail {
        return withCode(HttpStatus.NOT_FOUND, ex)
    }

    @ExceptionHandler(VerificationRateLimitException::class)
    fun handleVerificationRateLimit(ex: VerificationRateLimitException): ProblemDetail {
        return withCode(HttpStatus.TOO_MANY_REQUESTS, ex)
    }

    @ExceptionHandler(InvalidVerificationTokenException::class)
    fun handleInvalidVerificationToken(ex: InvalidVerificationTokenException): ProblemDetail {
        return withCode(HttpStatus.UNPROCESSABLE_ENTITY, ex)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception caught", ex)
        val body = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "Unexpected server error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
