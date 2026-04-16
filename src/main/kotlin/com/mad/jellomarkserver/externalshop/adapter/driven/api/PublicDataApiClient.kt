package com.mad.jellomarkserver.externalshop.adapter.driven.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class PublicDataApiClient(
    @Value("\${public-data.api-key:}") private val apiKey: String
) {
    private val log = LoggerFactory.getLogger(PublicDataApiClient::class.java)
    private val restTemplate = RestTemplate()

    fun fetchNailShopsInRadius(latitude: Double, longitude: Double, radiusMeters: Int = 5000): List<PublicShopData> {
        if (apiKey.isBlank()) {
            log.warn("Public data API key is not configured")
            return emptyList()
        }

        val allShops = mutableListOf<PublicShopData>()
        var pageNo = 1
        val numOfRows = 100

        while (true) {
            val url = UriComponentsBuilder
                .fromHttpUrl("$BASE_URL/storeListInRadius")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("radius", radiusMeters)
                .queryParam("cx", longitude)
                .queryParam("cy", latitude)
                .queryParam("indsSclsCd", NAIL_SHOP_CODE)
                .queryParam("type", "json")
                .build(false)
                .toUriString()

            try {
                val response = restTemplate.getForObject(url, Map::class.java) ?: break

                @Suppress("UNCHECKED_CAST")
                val body = response["body"] as? Map<String, Any> ?: break

                @Suppress("UNCHECKED_CAST")
                val items = body["items"] as? List<Map<String, Any>> ?: break

                if (items.isEmpty()) break

                val shops = items.mapNotNull { parseShopData(it) }
                allShops.addAll(shops)

                val totalCount = (body["totalCount"] as? Number)?.toInt() ?: 0
                if (pageNo * numOfRows >= totalCount) break

                pageNo++
            } catch (e: Exception) {
                log.error("Failed to fetch shops from public data API: page={}, error={}", pageNo, e.message)
                break
            }
        }

        log.info("Fetched {} nail shops from public data API (lat={}, lng={}, radius={}m)", allShops.size, latitude, longitude, radiusMeters)
        return allShops
    }

    private fun parseShopData(item: Map<String, Any>): PublicShopData? {
        val bizesId = item["bizesId"]?.toString() ?: return null
        val bizesNm = item["bizesNm"]?.toString() ?: return null
        val rdnmAdr = item["rdnmAdr"]?.toString() ?: item["lnoAdr"]?.toString() ?: ""
        val lat = (item["lat"] as? Number)?.toDouble() ?: return null
        val lon = (item["lon"] as? Number)?.toDouble() ?: return null
        val indsLclsNm = item["indsLclsNm"]?.toString() ?: ""
        val indsMclsNm = item["indsMclsNm"]?.toString() ?: ""
        val indsSclsNm = item["indsSclsNm"]?.toString() ?: ""
        val telNo = item["telNo"]?.toString()?.takeIf { it.isNotBlank() }

        return PublicShopData(
            externalId = bizesId,
            name = bizesNm,
            address = rdnmAdr,
            latitude = lat,
            longitude = lon,
            category = indsSclsNm.ifBlank { indsMclsNm.ifBlank { indsLclsNm } },
            phoneNumber = telNo
        )
    }

    companion object {
        private const val BASE_URL = "https://apis.data.go.kr/B553077/api/open/sdsc2"
        private const val NAIL_SHOP_CODE = "S20703"
    }
}

data class PublicShopData(
    val externalId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val category: String,
    val phoneNumber: String?
)
