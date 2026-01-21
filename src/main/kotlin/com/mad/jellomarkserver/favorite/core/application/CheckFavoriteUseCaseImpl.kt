package com.mad.jellomarkserver.favorite.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.favorite.port.driving.CheckFavoriteCommand
import com.mad.jellomarkserver.favorite.port.driving.CheckFavoriteUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CheckFavoriteUseCaseImpl(
    private val favoritePort: FavoritePort
) : CheckFavoriteUseCase {

    @Transactional(readOnly = true)
    override fun execute(command: CheckFavoriteCommand): Boolean {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        return favoritePort.existsByMemberIdAndShopId(memberId, shopId)
    }
}
