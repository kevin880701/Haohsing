package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.FlowUseCase
import com.clockworkorange.domain.Result
import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.entity.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDevicesUnderPlaceAreaUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<GetDevicesUnderPlaceAreaParam, List<Device>>(dispatcher){
    override fun execute(parameters: GetDevicesUnderPlaceAreaParam): Flow<Result<List<Device>>> {
        val (placeId, areaId) = parameters
        return deviceRepository.getDevicesUnderPlaceArea(placeId, areaId).map { Result.Success(it) }
    }

}

data class GetDevicesUnderPlaceAreaParam(
    val placeId: Int,
    val areaId: Int
)