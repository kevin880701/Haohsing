package com.clockworkorange.domain.usecase.cityregion

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.CityRegionRepository
import com.clockworkorange.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetCityListUseCase @Inject constructor(
    private val repository: CityRegionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<Unit, List<String>>(dispatcher) {

    override suspend fun execute(parameters: Unit): List<String> {
        return repository.getCityList()
    }
}