package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.AreaInfo
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetAreaListUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, List<AreaInfo>>(dispatcher) {

    override suspend fun execute(parameters: Int): List<AreaInfo> {
        return placeRepository.getAreaList(parameters)
    }
}
