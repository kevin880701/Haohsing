package com.clockworkorange.domain.usecase.device

import com.clockworkorange.domain.UseCase
import com.clockworkorange.domain.data.DeviceRepository
import com.clockworkorange.domain.data.remote.model.ChangeFilterParam
import com.clockworkorange.domain.data.remote.model.FilterParam
import com.clockworkorange.domain.di.IoDispatcher
import com.clockworkorange.domain.usecase.task.ChangedFilter
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDate
import javax.inject.Inject

class AddDeviceManuallyUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<AddDeviceManualUseCaseParam, Int>(dispatcher) {
    override suspend fun execute(parameters: AddDeviceManualUseCaseParam): Int {
        return deviceRepository.addDeviceManually(parameters)
    }
}

data class AddDeviceManualUseCaseParam(
    val areaId: Int? = null,
    val filters: List<ChangedFilter>? = null,
    val brand: String,
    val model: String,
    val name: String,
    val placeId: Int? = null,
    val vendorId: Int? = null,
    val customerAddress: String? = null,
    val customerAgencyId: Int? = null,
)