package com.mad.jellomarkserver.member.core.domain.exception

class DuplicateSocialAccountException(provider: String, socialId: String) :
    RuntimeException("Social account already exists: $provider:$socialId")
