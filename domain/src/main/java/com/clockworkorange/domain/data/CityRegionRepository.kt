package com.clockworkorange.domain.data

import com.clockworkorange.domain.data.remote.ServiceWrapper
import javax.inject.Inject

interface CityRegionRepository {
    suspend fun getCityList(): List<String>
    suspend fun getRegionList(city:String): List<String>
}

class CityRegionRepositoryImpl @Inject constructor(
    private val service: ServiceWrapper
): CityRegionRepository{

    override suspend fun getCityList(): List<String> {
        return service.getCityList()
    }

    override suspend fun getRegionList(city: String): List<String> {
        return service.getRegionList(city)
    }
}