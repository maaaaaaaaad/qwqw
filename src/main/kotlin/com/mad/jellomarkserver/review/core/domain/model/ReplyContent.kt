package com.mad.jellomarkserver.review.core.domain.model

import com.mad.jellomarkserver.review.core.domain.exception.InvalidReplyContentException

@JvmInline
value class ReplyContent private constructor(val value: String) {
    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 500

        fun of(content: String): ReplyContent {
            val trimmed = content.trim()
            if (trimmed.length !in MIN_LENGTH..MAX_LENGTH) {
                throw InvalidReplyContentException(trimmed)
            }
            return ReplyContent(trimmed)
        }
    }
}
