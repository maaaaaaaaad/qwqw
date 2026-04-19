package com.mad.jellomarkserver.member.core.domain.exception

class InvalidWithdrawalReasonException(reason: String) :
    RuntimeException("탈퇴 사유가 유효하지 않습니다: $reason")
