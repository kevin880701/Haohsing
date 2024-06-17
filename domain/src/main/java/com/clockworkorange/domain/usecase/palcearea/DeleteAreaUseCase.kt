package com.clockworkorange.domain.usecase.palcearea

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.PlaceRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteAreaUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Int, Boolean>(dispatcher) {

    override suspend fun execute(parameters: Int): Boolean {
        return placeRepository.deleteArea(parameters)
    }
}

