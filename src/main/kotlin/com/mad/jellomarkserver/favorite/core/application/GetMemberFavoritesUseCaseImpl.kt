package com.mad.jellomarkserver.favorite.core.application

import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.favorite.port.driving.GetMemberFavoritesCommand
import com.mad.jellomarkserver.favorite.port.driving.GetMemberFavoritesUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GetMemberFavoritesUseCaseImpl(
    private val favoritePort: FavoritePort
) : GetMemberFavoritesUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: GetMemberFavoritesCommand): Page<Favorite> {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val pageable = PageRequest.of(command.page, command.size, Sort.by(Sort.Direction.DESC, "createdAt"))

        return favoritePort.findByMemberId(memberId, pageable)
    }
}
