package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.core.domain.model.Beautishop
import com.mad.jellomarkserver.beautishop.core.domain.model.GpsDistanceCalculator
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopFilterCriteria
import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.*
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ListBeautishopsUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : ListBeautishopsUseCase {

    override fun execute(command: ListBeautishopsCommand): PagedBeautishops {
        val pageable = PageRequest.of(command.page, command.size)

        val criteria = BeautishopFilterCriteria(
            keyword = command.keyword,
            categoryId = command.categoryId,
            minRating = command.minRating,
            sortBy = command.sortBy,
            sortOrder = command.sortOrder,
            latitude = command.latitude,
            longitude = command.longitude
        )

        val page = beautishopPort.findAllFiltered(criteria, pageable)

        val itemsWithDistance = if (command.latitude != null && command.longitude != null) {
            page.content.map { beautishop ->
                BeautishopWithDistance(
                    beautishop = beautishop,
                    distance = GpsDistanceCalculator.calculateDistanceKm(
                        lat1 = command.latitude,
                        lon1 = command.longitude,
                        lat2 = beautishop.gps.latitude,
                        lon2 = beautishop.gps.longitude
                    )
                )
            }
        } else {
            page.content.map { BeautishopWithDistance(it, null) }
        }

        val sortedItems =
            if (command.sortBy == SortBy.DISTANCE && command.latitude != null && command.longitude != null) {
                when (command.sortOrder) {
                    SortOrder.ASC -> itemsWithDistance.sortedBy { it.distance }
                    SortOrder.DESC -> itemsWithDistance.sortedByDescending { it.distance }
                }
            } else {
                itemsWithDistance
            }

        return PagedBeautishops(
            items = sortedItems.map { it.beautishop },
            distances = sortedItems.map { it.distance },
            hasNext = page.hasNext(),
            totalElements = page.totalElements
        )
    }

    private data class BeautishopWithDistance(
        val beautishop: Beautishop,
        val distance: Double?
    )
}
