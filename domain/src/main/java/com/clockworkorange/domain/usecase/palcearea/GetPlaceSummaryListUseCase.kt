package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.PlaceInfo
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPlaceSummaryListUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, List<PlaceSummary>>(dispatcher){

    override suspend fun execute(parameters: Unit): List<PlaceSummary> {
        return placeRepository.getPlaceSummary()
    }
}

data class PlaceSummary(
    val id: Int,
    val name: String,
    val areaCount: Int,
    val shareCount: Int
){
    fun toPlaceInfo():PlaceInfo{
        return PlaceInfo(id, name)
    }
}