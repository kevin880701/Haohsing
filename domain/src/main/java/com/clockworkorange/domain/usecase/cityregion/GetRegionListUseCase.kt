package com.clockworkorange.domain.usecase.cityregion

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.CityRegionRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetRegionListUseCase @Inject constructor(
    private val repository: CityRegionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<String, List<String>>(dispatcher) {

    override suspend fun execute(city: String): List<String> {
        return repository.getRegionList(city)
    }
}