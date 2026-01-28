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
        val criteria = BeautishopFilterCriteria(
            keyword = command.keyword,
            categoryId = command.categoryId,
            minRating = command.minRating,
            sortBy = command.sortBy,
            sortOrder = command.sortOrder,
            latitude = command.latitude,
            longitude = command.longitude
        )

        val isDistanceSort = command.sortBy == SortBy.DISTANCE &&
            command.latitude != null && command.longitude != null

        if (isDistanceSort) {
            val allItems = beautishopPort.findAllFilteredWithoutPaging(criteria)

            val itemsWithDistance = allItems.map { beautishop ->
                BeautishopWithDistance(
                    beautishop = beautishop,
                    distance = GpsDistanceCalculator.calculateDistanceKm(
                        lat1 = command.latitude!!,
                        lon1 = command.longitude!!,
                        lat2 = beautishop.gps.latitude,
                        lon2 = beautishop.gps.longitude
                    )
                )
            }

            val sortedItems = when (command.sortOrder) {
                SortOrder.ASC -> itemsWithDistance.sortedBy { it.distance }
                SortOrder.DESC -> itemsWithDistance.sortedByDescending { it.distance }
            }

            val startIndex = command.page * command.size
            val endIndex = minOf(startIndex + command.size, sortedItems.size)
            val pagedItems = if (startIndex < sortedItems.size) {
                sortedItems.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

            return PagedBeautishops(
                items = pagedItems.map { it.beautishop },
                distances = pagedItems.map { it.distance },
                hasNext = endIndex < sortedItems.size,
                totalElements = sortedItems.size.toLong()
            )
        } else {
            val pageable = PageRequest.of(command.page, command.size)
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

            return PagedBeautishops(
                items = itemsWithDistance.map { it.beautishop },
                distances = itemsWithDistance.map { it.distance },
                hasNext = page.hasNext(),
                totalElements = page.totalElements
            )
        }
    }

    private data class BeautishopWithDistance(
        val beautishop: Beautishop,
        val distance: Double?
    )
}
