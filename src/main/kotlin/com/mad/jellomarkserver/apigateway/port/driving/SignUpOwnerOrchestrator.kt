package com.mad.jellomarkserver.apigateway.port.driving

import com.mad.jellomarkserver.apigateway.adapter.driving.web.request.SignUpOwnerRequest
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.SignUpResponse

fun interface SignUpOwnerOrchestrator {
    fun signUp(request: SignUpOwnerRequest): SignUpResponse
}
