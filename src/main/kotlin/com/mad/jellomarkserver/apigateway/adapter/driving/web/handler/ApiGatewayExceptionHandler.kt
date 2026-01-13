package com.mad.jellomarkserver.apigateway.adapter.driving.web.handler

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.ErrorResponse
import com.mad.jellomarkserver.auth.core.domain.exception.*
import com.mad.jellomarkserver.beautishop.core.domain.exception.*
import com.mad.jellomarkserver.category.core.domain.exception.CategoryNotFoundException
import com.mad.jellomarkserver.category.core.domain.exception.UnauthorizedShopAccessException
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateMemberNicknameException
import com.mad.jellomarkserver.review.core.domain.exception.*
import com.mad.jellomarkserver.member.core.domain.exception.DuplicateSocialAccountException
import com.mad.jellomarkserver.member.core.domain.exception.InvalidMemberNicknameException
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.owner.core.domain.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.mad.jellomarkserver.apigateway"])
class ApiGatewayExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException::class)
    fun handleAuthenticationFailed(ex: AuthenticationFailedException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
    }

    @ExceptionHandler(InvalidKakaoTokenException::class)
    fun handleInvalidKakaoToken(ex: InvalidKakaoTokenException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
    }

    @ExceptionHandler(KakaoApiException::class)
    fun handleKakaoApiException(ex: KakaoApiException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.message)
    }

    @ExceptionHandler(InvalidAuthEmailException::class)
    fun handleInvalidAuthEmail(ex: InvalidAuthEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateAuthEmailException::class)
    fun handleDuplicateAuthEmail(ex: DuplicateAuthEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidRawPasswordException::class)
    fun handleInvalidPassword(ex: InvalidRawPasswordException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateSocialAccountException::class)
    fun handleDuplicateSocialAccount(ex: DuplicateSocialAccountException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(DuplicateMemberNicknameException::class)
    fun handleDuplicateMemberNickname(ex: DuplicateMemberNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidMemberNicknameException::class)
    fun handleInvalidMemberNickname(ex: InvalidMemberNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(MemberNotFoundException::class)
    fun handleMemberNotFound(ex: MemberNotFoundException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)
    }

    @ExceptionHandler(DuplicateOwnerNicknameException::class)
    fun handleDuplicateOwnerNickname(ex: DuplicateOwnerNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerNicknameException::class)
    fun handleInvalidOwnerNickname(ex: InvalidOwnerNicknameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateOwnerBusinessNumberException::class)
    fun handleDuplicateOwnerBusinessNumber(ex: DuplicateOwnerBusinessNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerBusinessNumberException::class)
    fun handleInvalidOwnerBusinessNumber(ex: InvalidOwnerBusinessNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateOwnerPhoneNumberException::class)
    fun handleDuplicateOwnerPhoneNumber(ex: DuplicateOwnerPhoneNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerPhoneNumberException::class)
    fun handleInvalidOwnerPhoneNumber(ex: InvalidOwnerPhoneNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateOwnerEmailException::class)
    fun handleDuplicateOwnerEmail(ex: DuplicateOwnerEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidOwnerEmailException::class)
    fun handleInvalidOwnerEmail(ex: InvalidOwnerEmailException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidShopNameException::class)
    fun handleInvalidShopName(ex: InvalidShopNameException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidShopRegNumException::class)
    fun handleInvalidShopRegNum(ex: InvalidShopRegNumException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(DuplicateShopRegNumException::class)
    fun handleDuplicateShopRegNum(ex: DuplicateShopRegNumException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(InvalidShopPhoneNumberException::class)
    fun handleInvalidShopPhoneNumber(ex: InvalidShopPhoneNumberException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidShopAddressException::class)
    fun handleInvalidShopAddress(ex: InvalidShopAddressException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidShopGPSException::class)
    fun handleInvalidShopGPS(ex: InvalidShopGPSException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidOperatingTimeException::class)
    fun handleInvalidOperatingTime(ex: InvalidOperatingTimeException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidShopDescriptionException::class)
    fun handleInvalidShopDescription(ex: InvalidShopDescriptionException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidShopImageException::class)
    fun handleInvalidShopImage(ex: InvalidShopImageException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(BeautishopNotFoundException::class)
    fun handleBeautishopNotFound(ex: BeautishopNotFoundException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)
    }

    @ExceptionHandler(InvalidReviewRatingException::class)
    fun handleInvalidReviewRating(ex: InvalidReviewRatingException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidReviewContentException::class)
    fun handleInvalidReviewContent(ex: InvalidReviewContentException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(InvalidReviewImagesException::class)
    fun handleInvalidReviewImages(ex: InvalidReviewImagesException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.message)
    }

    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)
    }

    @ExceptionHandler(UnauthorizedReviewAccessException::class)
    fun handleUnauthorizedReviewAccess(ex: UnauthorizedReviewAccessException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.message)
    }

    @ExceptionHandler(DuplicateReviewException::class)
    fun handleDuplicateReview(ex: DuplicateReviewException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message)
    }

    @ExceptionHandler(CategoryNotFoundException::class)
    fun handleCategoryNotFound(ex: CategoryNotFoundException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message)
    }

    @ExceptionHandler(UnauthorizedShopAccessException::class)
    fun handleUnauthorizedShopAccess(ex: UnauthorizedShopAccessException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.message)
    }

    @ExceptionHandler(UnauthorizedBeautishopAccessException::class)
    fun handleUnauthorizedBeautishopAccess(ex: UnauthorizedBeautishopAccessException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "Unexpected server error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
