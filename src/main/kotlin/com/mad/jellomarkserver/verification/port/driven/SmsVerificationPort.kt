package com.mad.jellomarkserver.verification.port.driven

interface SmsVerificationPort {
    fun sendCode(phoneNumber: String)
    fun checkCode(phoneNumber: String, code: String): Boolean
}
