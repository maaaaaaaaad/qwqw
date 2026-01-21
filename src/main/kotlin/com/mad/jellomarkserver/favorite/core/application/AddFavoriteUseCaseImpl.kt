package com.mad.jellomarkserver.favorite.core.application

import com.mad.jellomarkserver.beautishop.core.domain.exception.BeautishopNotFoundException
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.favorite.core.domain.exception.DuplicateFavoriteException
import com.mad.jellomarkserver.favorite.core.domain.model.Favorite
import com.mad.jellomarkserver.favorite.port.driven.FavoritePort
import com.mad.jellomarkserver.favorite.port.driving.AddFavoriteCommand
import com.mad.jellomarkserver.favorite.port.driving.AddFavoriteUseCase
import com.mad.jellomarkserver.member.core.domain.model.MemberId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AddFavoriteUseCaseImpl(
    private val favoritePort: FavoritePort,
    private val beautishopPort: BeautishopPort
) : AddFavoriteUseCase {

    @Transactional
    override fun execute(command: AddFavoriteCommand): Favorite {
        val memberId = MemberId.from(UUID.fromString(command.memberId))
        val shopId = ShopId.from(UUID.fromString(command.shopId))

        beautishopPort.findById(shopId)
            ?: throw BeautishopNotFoundException(command.shopId)

        if (favoritePort.existsByMemberIdAndShopId(memberId, shopId)) {
            throw DuplicateFavoriteException(command.shopId, command.memberId)
        }

        val favorite = Favorite.create(
            memberId = memberId,
            shopId = shopId
        )

        return favoritePort.save(favorite)
    }
}
