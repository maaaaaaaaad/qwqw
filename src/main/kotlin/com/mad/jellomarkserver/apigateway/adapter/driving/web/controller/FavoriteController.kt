package com.mad.jellomarkserver.apigateway.adapter.driving.web.controller

import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.FavoriteResponse
import com.mad.jellomarkserver.apigateway.adapter.driving.web.response.PagedFavoritesResponse
import com.mad.jellomarkserver.beautishop.core.domain.model.ShopId
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.favorite.port.driving.*
import com.mad.jellomarkserver.member.core.domain.exception.MemberNotFoundException
import com.mad.jellomarkserver.member.core.domain.model.SocialId
import com.mad.jellomarkserver.member.port.driven.MemberPort
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class FavoriteController(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val getMemberFavoritesUseCase: GetMemberFavoritesUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    private val memberPort: MemberPort,
    private val beautishopPort: BeautishopPort
) {
    @PostMapping("/api/favorites/{shopId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addFavorite(
        @PathVariable shopId: String,
        servletRequest: HttpServletRequest
    ): FavoriteResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can add favorites")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = AddFavoriteCommand(
            memberId = member.id.value.toString(),
            shopId = shopId
        )

        val favorite = addFavoriteUseCase.execute(command)
        val shop = beautishopPort.findById(ShopId.from(UUID.fromString(shopId)))
        return FavoriteResponse.from(favorite, shop)
    }

    @DeleteMapping("/api/favorites/{shopId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeFavorite(
        @PathVariable shopId: String,
        servletRequest: HttpServletRequest
    ) {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can remove favorites")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = RemoveFavoriteCommand(
            memberId = member.id.value.toString(),
            shopId = shopId
        )

        removeFavoriteUseCase.execute(command)
    }

    @GetMapping("/api/favorites")
    fun getMemberFavorites(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        servletRequest: HttpServletRequest
    ): PagedFavoritesResponse {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can view favorites")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = GetMemberFavoritesCommand(
            memberId = member.id.value.toString(),
            page = page,
            size = size
        )

        val favoritesPage = getMemberFavoritesUseCase.execute(command)

        val shopIds = favoritesPage.content.map { ShopId.from(it.shopId.value) }
        val shops = beautishopPort.findByIds(shopIds)
        val shopMap = shops.associateBy { it.id.value.toString() }

        return PagedFavoritesResponse.from(favoritesPage, shopMap)
    }

    @GetMapping("/api/favorites/check/{shopId}")
    fun checkFavorite(
        @PathVariable shopId: String,
        servletRequest: HttpServletRequest
    ): Map<String, Boolean> {
        val identifier = servletRequest.getAttribute("email") as String
        val userType = servletRequest.getAttribute("userType") as String

        if (userType != "MEMBER") {
            throw IllegalStateException("Only members can check favorites")
        }

        val member = memberPort.findBySocialId(SocialId(identifier))
            ?: throw MemberNotFoundException(identifier)

        val command = CheckFavoriteCommand(
            memberId = member.id.value.toString(),
            shopId = shopId
        )

        val isFavorite = checkFavoriteUseCase.execute(command)
        return mapOf("isFavorite" to isFavorite)
    }
}
