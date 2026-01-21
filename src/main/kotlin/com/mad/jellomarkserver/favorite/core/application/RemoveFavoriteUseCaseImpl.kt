package com.mad.jellomarkserver.favorite.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.favorite.core.domain.exception.FavoriteNotFoundException
import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.favorite.port.driving.RemoveFavoriteCommand
import com.mad.jellomarkserver.favorite.port.driving.RemoveFavoriteUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RemoveFavoriteUseCaseImpl(
    private val favoritePort: FavoritePort
) : RemoveFavoriteUseCase {

    @Transactional
    override fun execute(command: RemoveFavoriteCommand) {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        if (!favoritePort.existsByMemberIdAndShopId(memberId, shopId)) {
            throw FavoriteNotFoundException(command.shopId, command.memberId)
        }

        favoritePort.deleteByMemberIdAndShopId(memberId, shopId)
    }
}
