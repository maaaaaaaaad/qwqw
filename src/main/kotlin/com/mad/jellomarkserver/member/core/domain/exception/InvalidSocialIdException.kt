package com.mad.jellomarkserver.member.core.domain.exception

class InvalidSocialIdException(socialId: String) : RuntimeException("Invalid social ID: $socialId")
