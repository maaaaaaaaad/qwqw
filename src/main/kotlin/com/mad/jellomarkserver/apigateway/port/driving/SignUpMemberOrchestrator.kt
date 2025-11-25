package com.mad.jellomarkserver.apigateway.port.driving

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpMemberRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse

fun interface SignUpMemberOrchestrator {
    fun signUp(request: SignUpMemberRequest): SignUpResponse
}
