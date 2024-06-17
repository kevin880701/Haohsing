package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.PlaceInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlaceListUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Unit, List<PlaceInfo>>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<List<PlaceInfo>>> {
        return placeRepository.getPlaceList().map { Result.Success(it) }
    }

}
