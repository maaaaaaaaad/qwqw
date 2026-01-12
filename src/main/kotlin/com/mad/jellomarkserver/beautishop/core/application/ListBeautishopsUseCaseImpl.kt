package com.mad.jellomarkserver.beautishop.core.application

import com.mad.jellomarkserver.beautishop.port.driven.BeautishopPort
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsCommand
import com.mad.jellomarkserver.beautishop.port.driving.ListBeautishopsUseCase
import com.mad.jellomarkserver.beautishop.port.driving.PagedBeautishops
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ListBeautishopsUseCaseImpl(
    private val beautishopPort: BeautishopPort
) : ListBeautishopsUseCase {

    override fun execute(command: ListBeautishopsCommand): PagedBeautishops {
        val pageable = PageRequest.of(command.page, command.size)
        val page = beautishopPort.findAllPaged(pageable)

        return PagedBeautishops(
            items = page.content,
            hasNext = page.hasNext(),
            totalElements = page.totalElements
        )
    }
}
