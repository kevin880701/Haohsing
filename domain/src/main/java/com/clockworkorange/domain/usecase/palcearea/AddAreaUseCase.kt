package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddAreaUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AddAreaParam, Int>(dispatcher) {
    override suspend fun execute(parameters: AddAreaParam): Int {
        return placeRepository.addArea(parameters.placeId, parameters.name)
    }
}

data class AddAreaParam(val placeId: Int, val name: String)